package com.highcharts.export.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import com.oreilly.servlet.MultipartRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.log4j.Logger;

import com.highcharts.export.util.MimeType;
import com.highcharts.export.util.SVGRasterizer;
import com.highcharts.export.util.SVGRasterizerException;

public class ExportController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String REQUEST_METHOD_POST = "POST";
	private static final String CONTENT_TYPE_MULTIPART = "multipart/";
	private static final String FORBIDDEN_WORD = "<!ENTITY";
	protected static Logger logger = Logger.getLogger("exportservlet");
	

	public ExportController() {
		super();
	}

	public void init() {		
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			throw new ServletException(
				"Get request are not handled");
		} catch (ServletException sce) {
			logger.error("Oops problem reading or parsing the request"
					+ sce.getMessage());
			sendError(response, sce);
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			MultipartRequest mr = new MultipartRequest(request, "/tmp");
			processrequest(mr, response);
		} catch (IOException ioe) {
			logger.error("Oops problem reading or parsing the request"
					+ ioe.getMessage());
			sendError(response, ioe);
		}
	}

	public void processrequest(MultipartRequest request,
			HttpServletResponse response) throws IOException, ServletException {

		try {
			String svg = request.getParameter( "svg" );
			if (svg == null || svg.isEmpty()) {
				throw new ServletException(
					"The required - svg - post parameter is missing");
			}
			
			if (svg.indexOf(FORBIDDEN_WORD) > -1 || svg.indexOf(FORBIDDEN_WORD.toLowerCase()) > -1){
				//throw new ServletException(
				//	"The - svg - post parameter could contain a malicious attack");
			}

			String filename = getFilename(request.getParameter( "filename" ));
			Float width = getWidth(request.getParameter( "width" ));
			MimeType mime = getMime(request.getParameter( "type" ));

			ExportController.writeFileContentToHttpResponse(svg, filename,
					width, mime, response);

		} catch (IOException ioe) {
			logger.error("Oops something happened here redirect to error-page, "
					+ ioe.getMessage());
			sendError(response, ioe);
		} catch (ServletException sce) {
			logger.error("Oops something happened here redirect to error-page, "
					+ sce.getMessage());
			sendError(response, sce);
		}
		
	}

	/*
	 * Util methods
	 */

	public static void writeFileContentToHttpResponse(String svg,
			String filename, Float width, MimeType mime,
			HttpServletResponse response) throws IOException, ServletException {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();

		if (!MimeType.SVG.equals(mime)) {
			try {
				stream = SVGRasterizer.getInstance().transcode(stream, svg,
						mime, width);
			} catch (SVGRasterizerException sre) {
				logger.error("Error while transcoding svg file to an image", sre);
				stream.close();
				throw new ServletException(
						"Error while transcoding svg file to an image");
			} catch (TranscoderException te){
				logger.error("Error while transcoding svg file to an image", te);
				stream.close();
				throw new ServletException(
						"Error while transcoding svg file to an image");
			}
		} else {
			stream.write(svg.getBytes());
		}

		// prepare response
		response.reset();
		response.setContentLength(stream.size());
		response.setCharacterEncoding("utf-8");
		response.setHeader("Content-disposition", "attachment; filename="
				+ filename + "." + mime.name().toLowerCase());
		response.setHeader("Content-type", mime.getType());
		// set encoding before writing to out, check this
		ServletOutputStream out = response.getOutputStream();
		// Send content to Browser
		out.write(stream.toByteArray());
		out.flush();
	}

	private String getFilename(String name) {
		return (name != null) ? name : "chart";
	}

	private static Float getWidth(String width) {
		if (width != null && !width.isEmpty()) {
			Float parsedWidth = Float.valueOf(width);
			if (parsedWidth.compareTo(0.0F) > 0) {
				return parsedWidth;
			}
		}
		return null;
	}

	private static MimeType getMime(String mime) {
		MimeType type = MimeType.get(mime);
		if (type != null) {
			return type;
		}
		return MimeType.PNG;
	}

	protected void sendError(HttpServletResponse response, Throwable ex) throws IOException,
			ServletException {
		String headers = null;
		String htmlHeader = "<HTML><HEAD><TITLE>Highcharts Export error</TITLE><style type=\"text/css\">"
				+ "body {font-family: \"Trebuchet MS\", Arial, Helvetica, sans-serif;} table {border-collapse: collapse;}th {background-color:green;color:white;} td, th {border: 1px solid #98BF21;} </style></HEAD><BODY>";
		String htmlFooter = "</BODY></HTML>";

		response.setContentType("text/html");

		PrintWriter out = response.getWriter();

		out.println(htmlHeader);
		out.println("<h3>Error while converting SVG</h3>");
		out.println("<h4>Error message</h4>");
		out.println("<p>" + ex.getMessage() + "</p>");
		out.println("</TABLE><BR>");
		out.println(htmlFooter);

	}
}
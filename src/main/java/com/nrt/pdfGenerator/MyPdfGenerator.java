package com.nrt.pdfGenerator;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Component;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.nrt.exception.GeneratePdfException;

import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
public class MyPdfGenerator {

	public byte[] htmlContentToPDF(String htmlData) {
		log.debug("html data" + htmlData);

		try {
			ITextRenderer renderer = new ITextRenderer();

			renderer.setDocumentFromString(htmlData);

			renderer.layout();

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

			renderer.createPDF(outputStream);

			return outputStream.toByteArray();
		} catch (Exception e) {
			throw new GeneratePdfException("Failed to create the pdf from html page " + e.getMessage());
		}

	}

	public void htmlToPdfConverter(String fileName, byte[] attachment) {

	}

}
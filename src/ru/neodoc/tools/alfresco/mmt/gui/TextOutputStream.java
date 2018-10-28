package ru.neodoc.tools.alfresco.mmt.gui;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.widgets.Text;

public class TextOutputStream extends OutputStream {

	protected Text outputText;
	
	public TextOutputStream(Text text) {
		super();
		this.outputText = text;
	}
	
	@Override
	public void write(int b) throws IOException {
		// TODO Auto-generated method stub
		outputText.append(String.valueOf((char)b));
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		super.write(b, off, len);
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		super.write(b);
	}
	
}

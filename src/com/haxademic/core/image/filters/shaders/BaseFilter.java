package com.haxademic.core.image.filters.shaders;

import com.haxademic.core.system.FileUtil;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.opengl.PShader;

public class BaseFilter {

	protected PShader shader;

	public BaseFilter(PApplet p, String shaderFilePath) {
		shader = p.loadShader(FileUtil.getFile(shaderFilePath));
		setTime(0);
	}

	public void applyTo(PGraphics pg) {
		pg.filter(shader);
	}
	
	public void applyTo(PApplet p) {
		p.filter(shader);
	}
	
	public void setTime(float time) {
		shader.set("time", time);
	}
	
}

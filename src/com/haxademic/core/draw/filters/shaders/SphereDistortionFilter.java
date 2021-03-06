package com.haxademic.core.draw.filters.shaders;

import processing.core.PApplet;

public class SphereDistortionFilter
extends BaseFilter {

	public static SphereDistortionFilter instance;
	
	public SphereDistortionFilter(PApplet p) {
		super(p, "shaders/filters/sphere-distortion.glsl");
		setAmplitude(1f);
	}
	
	public static SphereDistortionFilter instance(PApplet p) {
		if(instance != null) return instance;
		instance = new SphereDistortionFilter(p);
		return instance;
	}

	public void setAmplitude(float amplitude) {
		shader.set("amplitude", amplitude);
	}

}

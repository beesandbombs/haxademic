package com.haxademic.core.draw.util;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
import processing.core.PVector;

public class PShapeUtil {
	
	/**
	 * Clone based on a pshape.getTessellation() for a flattened copy of an svg
	 * @param p
	 * @param tesselation
	 * @param texture
	 * @return
	 */
	public static PShape clonePShape(PApplet p, PShape tesselation) {
		PShape newShape = p.createShape();
		newShape.beginShape(P.TRIANGLES);
		for (int i = 0; i < tesselation.getVertexCount(); i++) {
			PVector v = tesselation.getVertex(i);
			newShape.vertex(v.x, v.y);
		}
		newShape.endShape(P.CLOSE);
		newShape.setStroke(false);
		return newShape;
	}
	
	public static void addUVsToPShape(PShape s, float outerExtent) {
		s.setStroke(false);
		s.setTextureMode(P.NORMAL);
		for (int i = 0; i < s.getVertexCount(); i++) {
			PVector v = s.getVertex(i);
			s.setTextureUV(
					i, 
					P.map(v.x, -outerExtent, outerExtent, 0, 1f), 
					P.map(v.y, outerExtent, -outerExtent, 0, 1f)
			);
		}
	}

	/**
	 * Adds UV coordinates to an .obj PShape based on a texture and outer extent
	 * @param s
	 * @param img
	 * @param outerExtent
	 */
	public static void addTextureUVToObj(PShape s, PImage img, float outerExtent) {
		s.setStroke(false);
		s.setFill(255);
		s.setTextureMode(P.NORMAL);
		for (int j = 0; j < s.getChildCount(); j++) {
			for (int i = 0; i < s.getChild(j).getVertexCount(); i++) {
				PShape subShape = s.getChild(j);
				PVector v = subShape.getVertex(i);
				subShape.setTextureUV(
						i, 
						P.map(v.x, -outerExtent, outerExtent, 0, 1f), 
						P.map(v.y, outerExtent, -outerExtent, 0, 1f)
				);
			}
		}
		if(img != null) s.setTexture(img);
	}
	
	/**
	 * Finds the maximum size in any given direction. A basic but crappy way to figure out PShape size
	 * @param s
	 * @return
	 */
	public static void scaleSvgToExtent(PShape s, float newExtent) {
		float modelExtent = getSvgMaxExtent(s.getTessellation());
		s.scale(newExtent/modelExtent);
	}
	
//	/**
//	 * Finds the maximum vertex extent to translate and center the children
//	 * @param s
//	 * @return
//	 */
//	public static void centerSvg(PShape s) {
//		float[] extents = {0,0,0,0,0,0};
//		PShape tellel = s.getTessellation();
//		checkShapeExtents(tellel, extents);
//		P.println("extents: ",extents[0],extents[1],extents[2],extents[3],extents[4],extents[5]);
//	}
//	
//	public static void checkShapeExtents(PShape s, float[] extents) {
//		for (int i = 0; i < s.getVertexCount(); i++) {
//			PVector vertex = s.getVertex(i);
//			if(extents[0] == 0 || vertex.x < extents[0]) extents[0] = vertex.x;
//			if(extents[1] == 0 || vertex.x > extents[1]) extents[1] = vertex.x;
//			if(extents[2] == 0 || vertex.y < extents[2]) extents[2] = vertex.y;
//			if(extents[3] == 0 || vertex.y > extents[3]) extents[3] = vertex.y;
//			if(extents[4] == 0 || vertex.z < extents[4]) extents[4] = vertex.z;
//			if(extents[5] == 0 || vertex.z > extents[5]) extents[5] = vertex.z;
//		}
//		for (int i = 0; i < s.getChildCount(); i++) {
//			PShape subShape = s.getChild(i);
////			P.println("subShape", subShape.getWidth(), subShape.getHeight());
//			checkShapeExtents(subShape, extents);
//		}
//	}
	
	/**
	 * Finds the maximum size in any given direction. A basic but crappy way to figure out PShape size
	 * @param s
	 * @return
	 */
	public static float scaleObjToExtent(PShape s, float newExtent) {
		float modelExtent = getObjMaxExtent(s);
		float newScale = newExtent/modelExtent;
		s.scale(newScale);
		return newScale;
	}
	
	
	// re-scale by displacing actual vertices
	public static void scaleObjToExtentVerticesAdjust(PShape s, float newExtent) {
		float modelExtent = getObjMaxExtent(s);
		float newScale = newExtent/modelExtent;
		adjustVertices(s, newScale);
	}
	
	public static void adjustVertices(PShape s, float scale) {
		for (int j = 0; j < s.getChildCount(); j++) {
			for (int i = 0; i < s.getChild(j).getVertexCount(); i++) {
				PShape subShape = s.getChild(j);
				PVector curVertex = subShape.getVertex(i);
				subShape.setVertex(i, curVertex.x * scale, curVertex.y * scale, curVertex.z * scale);
				adjustVertices(subShape, scale);
			}
		}
	}
	
	
	public static float scaleObjToExtentReturnScale(PShape s, float newExtent) {
		float modelExtent = getObjMaxExtent(s);
		float newScale = newExtent / modelExtent;
		s.scale(newScale);
		return newScale;
	}
	
	/**
	 * Finds the maximum size in any given direction. A basic but crappy way to figure out PShape size
	 * @param s
	 * @return
	 */
	public static float getObjMaxExtent(PShape s) {
		// find mesh size extent to responsively scale the mesh
		float outermostVertex = 0;
		for (int j = 0; j < s.getChildCount(); j++) {
			for (int i = 0; i < s.getChild(j).getVertexCount(); i++) {
				PShape subShape = s.getChild(j);
				PVector vertex = subShape.getVertex(i);
				if(vertex.x > outermostVertex) outermostVertex = vertex.x;
				if(vertex.y > outermostVertex) outermostVertex = vertex.y;
				if(vertex.z > outermostVertex) outermostVertex = vertex.z;
			}
		}
		return outermostVertex;
	}
	
	/**
	 * Finds the maximum size in any given direction. A basic but crappy way to figure out PShape size
	 * @param s
	 * @return
	 */
	public static float getSvgMaxExtent(PShape s) {
		// find mesh size extent to responsively scale the mesh
		float outermostVertex = 0;
		for (int i = 0; i < s.getVertexCount(); i++) {
			PVector vertex = s.getVertex(i);
			if(vertex.x > outermostVertex) outermostVertex = vertex.x;
			if(vertex.y > outermostVertex) outermostVertex = vertex.y;
			if(vertex.z > outermostVertex) outermostVertex = vertex.z;
		}
		return outermostVertex;
	}
	
	/**
	 * Draws triangles instead of native draw calls
	 * @param s
	 * @return
	 */
	public static void drawTriangles(PApplet p, PShape s) {
		drawTriangles(p.g, s);
	}
	public static void drawTriangles(PGraphics p, PShape s) {
		for (int i = 0; i < s.getVertexCount() - 3; i += 3) { // ugh
			p.beginShape(P.TRIANGLES);
//			P.println(s.getVertex(i).x, s.getVertex(i).y, s.getVertex(i).z);
			p.vertex(s.getVertex(i).x, s.getVertex(i).y, s.getVertex(i).z);
			p.vertex(s.getVertex(i+1).x, s.getVertex(i+1).y, s.getVertex(i+1).z);
			p.vertex(s.getVertex(i+2).x, s.getVertex(i+2).y, s.getVertex(i+2).z);
			p.endShape();
		}
		for (int j = 0; j < s.getChildCount(); j++) {
			PShape subShape = s.getChild(j);
			drawTriangles(p, subShape);
		}
	}
	
	/**
	 * Draws triangles instead of native draw calls
	 * @param s
	 * @return
	 */
	public static void drawTrianglesWithTexture(PGraphics p, PShape s, PImage img, float scale) {
		p.fill(255);
		P.println(s.getChildCount());
		p.beginShape(PConstants.TRIANGLES);
		p.texture(img);
		p.textureMode(PConstants.NORMAL);

		for (int j = 0; j < s.getChildCount(); j++) {
			for (int i = 0; i < s.getChild(j).getVertexCount(); i++) {
				if(i+2 < s.getChild(j).getVertexCount()) {	// protect against rogue vertices?
					PVector vertex = s.getChild(j).getVertex(i);
					PVector vertex2 = s.getChild(j).getVertex(i+1);
					PVector vertex3 = s.getChild(j).getVertex(i+2);
					vertex.mult(scale);
					vertex2.mult(scale);
					vertex3.mult(scale);
					p.vertex(vertex.x, vertex.y, vertex.z, s.getChild(j).getTextureU(i), s.getChild(j).getTextureV(i));
					p.vertex(vertex2.x, vertex2.y, vertex2.z, s.getChild(j).getTextureU(i+1), s.getChild(j).getTextureV(i+1));
					p.vertex(vertex3.x, vertex3.y, vertex3.z, s.getChild(j).getTextureU(i+2), s.getChild(j).getTextureV(i+2));
				}
			}
		}
		p.endShape();

//		for (int j = 0; j < s.getChildCount(); j++) {
//			PShape subShape = s.getChild(j);
//			drawTrianglesWithTexture(p, subShape, img);
//		}
	}
	
	
	public static void drawTrianglesGrouped(PGraphics p, PShape s, float scale) {
		p.beginShape(PConstants.TRIANGLES);
		for (int j = 0; j < s.getChildCount(); j++) {
			for (int i = 0; i < s.getChild(j).getVertexCount(); i++) {
				if(i+2 < s.getChild(j).getVertexCount()) {	// protect against rogue vertices?
					PVector vertex = s.getChild(j).getVertex(i);
					PVector vertex2 = s.getChild(j).getVertex(i+1);
					PVector vertex3 = s.getChild(j).getVertex(i+2);
					vertex.mult(scale);
					vertex2.mult(scale);
					vertex3.mult(scale);
					p.vertex(vertex.x, vertex.y, vertex.z, s.getChild(j).getTextureU(i), s.getChild(j).getTextureV(i));
					p.vertex(vertex2.x, vertex2.y, vertex2.z, s.getChild(j).getTextureU(i+1), s.getChild(j).getTextureV(i+1));
					p.vertex(vertex3.x, vertex3.y, vertex3.z, s.getChild(j).getTextureU(i+2), s.getChild(j).getTextureV(i+2));
				}
			}
		}
		p.endShape();
	}

	// from @hamoid: https://twitter.com/hamoid/status/816682493793472512
	// not tested yet
	public static void exportMesh(PShape mesh) {
		StringBuilder verts = new StringBuilder();
		StringBuilder faces = new StringBuilder();
		final int vertsNum = mesh.getVertexCount();
		final PVector v = new PVector();
		for(int i=0; i < vertsNum; i+=3) {
			mesh.getVertex(i, v);
			verts.append("v " + v.x + " " + v.y + " " + v.z + "\n");
			mesh.getVertex(i+1, v);
			verts.append("v " + v.x + " " + v.y + " " + v.z + "\n");
			mesh.getVertex(i+2, v);
			verts.append("v " + v.x + " " + v.y + " " + v.z + "\n");
			faces.append("f " + (i+1) + " " + (i+2) + " " + (i+3) + "\n");
		}
		String outputStr = "o Sphere\n";
		outputStr += verts;
		outputStr += faces;
		FileUtil.writeTextToFile(FileUtil.getHaxademicOutputPath() + "text/model-"+SystemUtil.getTimestamp(P.p)+".obj", outputStr);
	}

}

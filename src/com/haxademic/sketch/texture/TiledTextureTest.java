package com.haxademic.sketch.texture;

import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.draw.image.TiledTexture;
import com.haxademic.core.file.FileUtil;

public class TiledTextureTest
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected TiledTexture tiledImg;
	protected int frames = 1220;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.WIDTH, 1000 );
		p.appConfig.setProperty( AppSettings.HEIGHT, 600 );
		p.appConfig.setProperty( AppSettings.FULLSCREEN, false );
		p.appConfig.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
	}

	public void setup() {
		super.setup();
		tiledImg = new TiledTexture(p.loadImage(FileUtil.getFile("images/bread.png")));
	}
	
	public void drawApp() {
		p.background(255);
		p.noStroke();
		
		float progress = (float)(p.frameCount % frames) / frames;
//		float easedPercent = Penner.easeInOutQuart(progress % 1, 0, 1, 1);
		float progressRads = progress * P.TWO_PI;

	
		p.pushMatrix();
		p.translate(p.width/2, p.height/2);
		float rot = (P.floor(p.frameCount / 30f) % 2 == 0) ? 0 : P.sin(progressRads);
		float size = 1.5f + 0.5f * P.sin(progressRads);
//		rot = p.frameCount * 0.03f;
//		tiledImg.setRotation(P.sin(progressRads * 2f) * 0.1f);
		tiledImg.setRotation(rot);
		tiledImg.setOffset(0.5f * P.sin(progressRads), 0.5f * P.cos(progressRads * 2f));
		tiledImg.setSize(size, size);
		tiledImg.update();
		tiledImg.drawCentered(p.g, p.width, p.height);
		p.popMatrix();
		
		
		tiledImg.drawDebug(p.g);
	}	

}
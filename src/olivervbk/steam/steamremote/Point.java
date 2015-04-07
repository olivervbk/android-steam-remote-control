package olivervbk.steam.steamremote;

import android.view.MotionEvent;

public class Point{
	public Float x;
	public Float y;
	
	public static final Point CLICK = new Point(Float.NaN, Float.NaN);
	public static final Point ORIGIN = new Point(0f, 0f);
	
	public Point( Float x, Float y){
		this.x = x;
		this.y = y;
	}
	
	public boolean isClick(){
		return x.isNaN() && y.isNaN();
	}
	
	public Point( MotionEvent event){
		this.x = event.getX();
		this.y = event.getY();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Point){
			final Point op = (Point) o;
			
			final int xInt = this.x.intValue();
			final int opXInt = op.x.intValue();
			if(xInt == opXInt){
				final int yInt = this.y.intValue();
				final int opYInt = op.y.intValue();
				if(yInt == opYInt){
					return true;
				}
			}
			
			return false;
		}
		return super.equals(o);
	}
	
	public Point diff(Point otherPoint){
		final Float xDiff = this.x - otherPoint.x;
		final Float yDiff = this.y - otherPoint.y;
		final Point diffPoint = new Point( xDiff, yDiff);
		return diffPoint;
	}
	
	@Override
	public String toString() {
		return "("+this.x+","+this.y+")";
	}
}
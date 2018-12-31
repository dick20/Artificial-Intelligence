
public class Piece implements Cloneable {
	public String Info;
	public char color;
	public char character;
	public char index;
	public int[] pos = new int[2];
	
	public Piece(String Info, int[] pos) {
		this.Info = Info;
		this.color = Info.charAt(0);
		this.character = Info.charAt(1);
		this.index = Info.charAt(2);
		this.pos = pos;
	}
}

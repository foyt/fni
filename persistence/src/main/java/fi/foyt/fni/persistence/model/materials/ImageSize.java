package fi.foyt.fni.persistence.model.materials;

public enum ImageSize {
  ORIGINAL (-1, -1),
  _16x16   (16, 16),
  _32x32   (32, 32),
  _48x48   (48, 48),
  _64x64   (64, 64),
  _128x128 (128, 128),
  _256x256 (256, 256);
  
  ImageSize(int width, int height) {
    this.width = width;
    this.height = height;
  }
  
  public int getHeight() {
    return height;
  }
  
  public int getWidth() {
    return width;
  }
  
  private int width;
  private int height;
}
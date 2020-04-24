class Obstacle {
  float xi, xf, yi, yf;

  Obstacle(float xi_, float yi_, float xf_, float yf_) {
    xi = xi_;
    yi = yi_;
    xf = xf_;
    yf = yf_;
  }

  void show() {
    fill(255, 150);
    rect(xi, yi, xf - xi, yf - yi);
  }
}

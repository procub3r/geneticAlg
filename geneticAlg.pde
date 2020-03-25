int popnum = 50, lifespan = 200, timer = 0, generation = 1, trgetHits = 0;
float mutationRate = 0.01, initialX, initialY, finalX, finalY;
PVector target;
Population population;
boolean pressed = false;
ArrayList<Obstacle> obstacles;

void setup() {
  size(1200, 600);
  target = new PVector(width / 2, 20);
  population = new Population(popnum, lifespan, target);
  obstacles = new ArrayList<Obstacle>();
}

void draw() {
  background(51);
  drawTarget();
  population.doYourThang();
  drawObstacles();
  showInfo();
}

void drawTarget() {
  noStroke();
  fill(0, 255, 0);
  ellipse(target.x, target.y, 10, 10);
}

void drawObstacles() {
  if (pressed && initialX != -1) {
    stroke(255, 150);
    noFill();
    rect(initialX, initialY, mouseX - initialX, mouseY - initialY);
  }

  for (Obstacle obs : obstacles) {
    obs.show();
  }
}

void mousePressed() {
  initialX = mouseX;
  initialY = mouseY;
  pressed = true;
}

void mouseReleased() {
  finalX = mouseX;
  finalY = mouseY;
  obstacles.add(new Obstacle(initialX, initialY, finalX, finalY));
  initialX = -1;
  initialY = -1;
  pressed = false;
}

void showInfo() {
  textSize(25);
  fill(255, 200);
  text("Gen: " + generation, 10, 25);
}

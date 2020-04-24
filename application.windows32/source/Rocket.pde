class Rocket {
  int lifespan;
  float fitness;
  PVector pos, vel, acc, target;
  DNA dna;
  boolean reached, crashed;
  color fillcolor = color(255, 150);

  Rocket(int lifespan_, PVector target_) {
    reached = false;
    crashed = false;
    lifespan = lifespan_;
    fitness = 0;
    target = target_;
    pos = new PVector(width / 2, height - 20);
    vel = new PVector(0, 0);
    acc = new PVector(0, 0);
    dna = new DNA(lifespan);
  }

  void calcFitness() {
    fitness = 1 / (pos.dist(target) + 0.001); // fitness equals 1 over the distance between rocket and target.
    if (reached) fitness *= 25;
    if (crashed) fitness /= 10;
  }

  void show() {
    push();
    translate(pos.x, pos.y);
    rotate(vel.heading() + (PI / 2));
    beginShape(TRIANGLES);
    fill(fillcolor);
    vertex(0, 0);
    vertex(-5, 15);
    vertex(5, 15);
    endShape(CLOSE);
    pop();

    //push();
    //translate(pos.x, pos.y);
    //stroke(255, 150);
    //noFill();
    //rectMode(CENTER);
    //rotate(vel.heading() + (PI / 2));
    //rect(0, 0, 10, 50);
    //pop();
  }

  void update() {
    applyForce(dna.genes[timer]);
    vel.add(acc);
    pos.add(vel);
    acc.mult(0);
  }

  void applyForce(PVector force) {
    acc.add(force);
  }

  void checkMotion() {
    if (pos.x > width || pos.x < 0 || pos.y > height || pos.y < 0) {
      crashed = true;
      fillcolor = color(255, 0, 0, 150);
    }

    // Check if the rocket hit an obstacle or not:
    for (Obstacle obs : obstacles) {
      if (obs.yi < obs.yf) {
        if (pos.y > obs.yi && pos.y < obs.yf) {
          if (obs.xi < obs.xf) {
            if (pos.x > obs.xi && pos.x < obs.xf) {
              crashed = true;
              fillcolor = color(255, 0, 0, 150);
            }
          } else {
            if (pos.x < obs.xi && pos.x > obs.xf) {
              crashed = true;
              fillcolor = color(255, 0, 0, 150);
            }
          }
        }
      } else {
        if (pos.y < obs.yi && pos.y > obs.yf) {
          if (obs.xi < obs.xf) {
            if (pos.x > obs.xi && pos.x < obs.xf) {
              crashed = true;
              fillcolor = color(255, 0, 0, 150);
            }
          } else {
            if (pos.x < obs.xi && pos.x > obs.xf) {
              crashed = true;
              fillcolor = color(255, 0, 0, 150);
            }
          }
        }
      }
    }

    if (pos.dist(target) <= 10) {
      pos = target;
      reached = true;
      fillcolor = color(0, 255, 0, 150);
    }
  }

  void reset() {
    reached = false;
    crashed = false;
    fitness = 0;
    pos = new PVector(width / 2, height - 20);
    vel = new PVector(0, 0);
    acc = new PVector(0, 0);
    fillcolor = color(255, 150);
  }

  boolean reached() {
    return (pos.dist(target) <= 10);
  }

  boolean crashed() {
    return (pos.x > width || pos.x < 0 || pos.y > height || pos.y < 0);
  }
}

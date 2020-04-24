import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class geneticAlg extends PApplet {

int popnum = 150, lifespan = 200, timer = 0, generation = 1;
float mutationRate = 0.01f;
PVector target;
Population population;

// Variables for obstacles
float initialX, initialY, finalX, finalY;
boolean pressed = false;
ArrayList<Obstacle> obstacles;

public void setup() {
  
  target = new PVector(width / 2, 20);
  population = new Population(popnum, lifespan, target);
  obstacles = new ArrayList<Obstacle>();
}

public void draw() {
  background(51);
  drawTarget();
  population.doYourThang();
  drawObstacles();
  showInfo();
}

public void drawTarget() {
  noStroke();
  fill(0, 255, 0);
  ellipse(target.x, target.y, 10, 10);
}

public void drawObstacles() {
  if (pressed && initialX != -1) {
    stroke(255, 150);
    noFill();
    rect(initialX, initialY, mouseX - initialX, mouseY - initialY);
  }

  for (Obstacle obs : obstacles) {
    obs.show();
  }
}

public void mousePressed() {
  initialX = mouseX;
  initialY = mouseY;
  pressed = true;
}

public void mouseReleased() {
  finalX = mouseX;
  finalY = mouseY;
  obstacles.add(new Obstacle(initialX, initialY, finalX, finalY));
  initialX = -1;
  initialY = -1;
  pressed = false;
}

public void showInfo() {
  textSize(25);
  fill(255, 200);
  text("Gen: " + generation, 10, 25);
}
class DNA {
  int lifespan;
  PVector[] genes;

  DNA(int lifespan_) {
    lifespan = lifespan_;
    genes = new PVector[lifespan];
    for (int i = 0; i < lifespan; i++) {
      genes[i] = PVector.random2D();
    }
  }

  public DNA crossover(DNA partner) {
    DNA child = new DNA(lifespan);
    int midpoint = (int)(genes.length / 2);
    for (int i = 0; i < genes.length; i++) {
      if (i <= midpoint) {
        child.genes[i] = genes[i];
      } else {
        child.genes[i] = partner.genes[i];
      }
    }
    return child;
  }

  public void mutate() {
    for (int i = 0; i < genes.length; i++) {
      if (random(1) < mutationRate) { // mutationRate is defines at the start of the sketch before the setup().
        int index = floor(random(genes.length));
        genes[index] = PVector.random2D().setMag(0.01f);
      }
    }
  }
}
class Obstacle {
  float xi, xf, yi, yf;

  Obstacle(float xi_, float yi_, float xf_, float yf_) {
    xi = xi_;
    yi = yi_;
    xf = xf_;
    yf = yf_;
  }

  public void show() {
    fill(255, 150);
    rect(xi, yi, xf - xi, yf - yi);
  }
}
class Population {
  int popnum, lifespan;
  PVector target;
  Rocket[] rockets;

  Population(int popnum_, int lifespan_, PVector target_) {
    popnum = popnum_;
    lifespan = lifespan_;
    target = target_;
    rockets = new Rocket[popnum];
    for (int i = 0; i < popnum; i++) {
      rockets[i] = new Rocket(lifespan, target);
    }
  }

  public void doYourThang() {
    for (int i = 0; i < popnum; i++) {
      rockets[i].checkMotion();
      if (!rockets[i].crashed && !rockets[i].reached) {        
        rockets[i].update();
      }
      rockets[i].show();
    }
    if (timer < lifespan - 1) timer++; 
    else {
      timer = 0;
      population.evolve();
      generation++;
    }
  }

  public void evolve() {
    // Perform natural selection -> select parents and populate the rockets array with new baby rockets:
    DNA[] newDNA = new DNA[popnum];
    for (int i = 0; i < popnum; i++) {
      newDNA[i] = naturalSelection();
    }
    // Creating a new Generation of rockets:
    for (int i = 0; i < popnum; i++) {
      rockets[i].reset();
      rockets[i].dna = newDNA[i];
    }
  }

  public DNA naturalSelection() {
    // Calculate the fitness of every rocket, and also compute the highest fitness value:
    float maxFitness = 0;
    for (int i = 0; i < popnum; i++) {
      rockets[i].calcFitness();
      if (rockets[i].fitness > maxFitness) {
        maxFitness = rockets[i].fitness;
      }
    }

    int indexA = selectParent(maxFitness);
    int indexB = selectParent(maxFitness);

    while (true) {
      if (indexB == indexA) {
        indexB = selectParent(maxFitness);
      } else {
        break;
      }
    }

    DNA parentA = rockets[indexA].dna;
    DNA parentB = rockets[indexB].dna;
    DNA child = parentA.crossover(parentB);
    child.mutate();

    return child;
  }

  public int selectParent(float maxFitness) {
    boolean picked = false;
    int index = 0;
    int counter = 0;
    while (!picked) {
      index = floor(random(rockets.length));
      if (random(maxFitness) < rockets[index].fitness) {
        picked = true;
      }
      counter++;
      if (counter >= 1000) {
        index = 0;
        picked = true;
      }
    }
    return index;
  }
}
class Rocket {
  int lifespan;
  float fitness;
  PVector pos, vel, acc, target;
  DNA dna;
  boolean reached, crashed;
  int fillcolor = color(255, 150);

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

  public void calcFitness() {
    fitness = 1 / (pos.dist(target) + 0.001f); // fitness equals 1 over the distance between rocket and target.
    if (reached) fitness *= 25;
    if (crashed) fitness /= 10;
  }

  public void show() {
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

  public void update() {
    applyForce(dna.genes[timer]);
    vel.add(acc);
    pos.add(vel);
    acc.mult(0);
  }

  public void applyForce(PVector force) {
    acc.add(force);
  }

  public void checkMotion() {
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

  public void reset() {
    reached = false;
    crashed = false;
    fitness = 0;
    pos = new PVector(width / 2, height - 20);
    vel = new PVector(0, 0);
    acc = new PVector(0, 0);
    fillcolor = color(255, 150);
  }

  public boolean reached() {
    return (pos.dist(target) <= 10);
  }

  public boolean crashed() {
    return (pos.x > width || pos.x < 0 || pos.y > height || pos.y < 0);
  }
}
  public void settings() {  size(1200, 600); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "geneticAlg" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

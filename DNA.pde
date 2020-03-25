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

  DNA crossover(DNA partner) {
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

  void mutate() {
    for (int i = 0; i < genes.length; i++) {
      if (random(1) < mutationRate) { // mutationRate is defines at the start of the sketch before the setup().
        int index = floor(random(genes.length));
        genes[index] = PVector.random2D().setMag(0.01);
      }
    }
  }
}

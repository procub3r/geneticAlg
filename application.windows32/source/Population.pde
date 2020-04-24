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

  void doYourThang() {
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

  void evolve() {
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

  DNA naturalSelection() {
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

  int selectParent(float maxFitness) {
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

package com.springboot.teamalbam.training.Repository;

import com.springboot.teamalbam.training.Entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepository extends JpaRepository<Training, Long> {
}


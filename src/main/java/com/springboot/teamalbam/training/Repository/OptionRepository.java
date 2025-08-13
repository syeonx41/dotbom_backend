package com.springboot.teamalbam.training.Repository;

import com.springboot.teamalbam.training.Entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByTrainingId(Long trainingId);
}

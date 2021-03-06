package ru.goryachev.multichief.construction.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.goryachev.multichief.construction.app.AppWebInit;
import ru.goryachev.multichief.construction.model.entity.Estimate;

import java.util.List;
import java.util.Optional;

/**
 * CRUD JPA Repository testing with real DB.
 * Necessary to use Lifecycle.PER_CLASS (@TestInstance) to work with common id (testId variable is end-to-end id).
 * An entity with autogenerated ID suppose to be created in the first method (1).
 * The ID will be used in each method (2,4,5).
 * The entity with ID suppose to be deleted in the last one (5).
 * Setting WebEnvironment.NONE does not start the embedded servlet container (MultiChief does not use embedded).
 * @author Lev Goryachev
 * @version 1.0
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(classes = AppWebInit.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EstimateRepositoryTest {

    Long testId;

    @Autowired
    private EstimateRepository estimateRepository;

    @Test
    @Order(1)
    @Rollback(value = false)
    public void saveEstimateTest(){
        Estimate estimate = new Estimate();
        estimate.setEstimateCodeNumber("testValueAbc");
        estimate.setLink("testValueDef");
        
        Estimate savedEstimate = estimateRepository.save(estimate);
        this.testId = savedEstimate.getId();
        Assertions.assertThat(savedEstimate.getId()).isGreaterThan(0);
    }

    @Test
    @Order(2)
    public void getEstimateTest(){
        Estimate estimate = estimateRepository.findById(testId).get();
        Assertions.assertThat(estimate.getId()).isEqualTo(testId);
    }
    
    @Test
    @Order(3)
    public void getAllEstimatesTest(){
        List<Estimate> estimates = estimateRepository.findAll();
        Assertions.assertThat(estimates.size()).isGreaterThan(0);
    }

    @Test
    @Order(4)
    @Rollback(value = false)
    public void updateEstimateTest(){
        Estimate estimate = estimateRepository.findById(testId).get();
        estimate.setEstimateCodeNumber("testValueChanged");
        Estimate estimateUpdated =  estimateRepository.save(estimate);
        Assertions.assertThat(estimateUpdated.getEstimateCodeNumber()).isEqualTo("testValueChanged");
    }

    @Test
    @Order(5)
    @Rollback(value = false)
    public void deleteEstimateTest(){

        Estimate estimate = estimateRepository.findById(testId).get();
        estimateRepository.delete(estimate);

        //estimateRepo.deleteById(1L);

        Estimate estimateEmpty = null;
        Optional<Estimate> optionalEstimate = estimateRepository.findById(testId);
        if(optionalEstimate.isPresent()){
            estimateEmpty = optionalEstimate.get();
        }
        Assertions.assertThat(estimateEmpty).isNull();
    }
}
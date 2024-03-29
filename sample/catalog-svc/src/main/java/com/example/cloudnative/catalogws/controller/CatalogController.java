package com.example.cloudnative.catalogws.controller;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cloudnative.catalogws.entity.CatalogEntity;
import com.example.cloudnative.catalogws.model.CatalogRequestModel;
import com.example.cloudnative.catalogws.model.CatalogResponseModel;
import com.example.cloudnative.catalogws.service.CatalogService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/catalog-ms")
public class CatalogController {
	 
    private final CatalogService catalogService;
    
    @GetMapping("/")
    public String health() {
        return "Hi, there. I'm a Catalog microservice!";
    }

    @GetMapping(value="/catalogs")
    public ResponseEntity<List<CatalogResponseModel>> getCatalogs() {
    	log.info("getCatalogs");
        Iterable<CatalogEntity> catalogList = catalogService.getAllCatalogs();
        List<CatalogResponseModel> result = new ArrayList<>();
        catalogList.forEach(v -> {
            result.add(new ModelMapper().map(v, CatalogResponseModel.class));
        });
        
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    
    @PostMapping(value="/catalog")
    public ResponseEntity<CatalogRequestModel> setCatalog(@RequestBody CatalogRequestModel catalogRequestModel) throws JsonProcessingException {
		
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        CatalogEntity catalogEntity = modelMapper.map(catalogRequestModel, CatalogEntity.class);
        catalogEntity.setCreatedAt(new Date());
        catalogService.setCatalog(catalogEntity);

        return ResponseEntity.status(HttpStatus.OK).body(catalogRequestModel);
    }
    
    @GetMapping(value="/catalog/{productId}")
    public ResponseEntity<CatalogResponseModel> getCatalog(@PathVariable("productId") String productId) throws JsonMappingException, JsonProcessingException {
    	log.info("getCatalogs");
    	
    	CatalogEntity catalogEntity = catalogService.getCatalog(productId);
        
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        CatalogResponseModel catalogResponseModel = modelMapper.map(catalogEntity, CatalogResponseModel.class);
        
        return ResponseEntity.status(HttpStatus.OK).body(catalogResponseModel);
    }
    
    
    @GetMapping(value="/catalog/delete-catalog")
    public ResponseEntity<Void> deleteCatalog() {
    	log.info("delete Catalogs");
    	
    	catalogService.deleteCatalog();
    
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
    
}

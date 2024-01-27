package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        if(webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName()) !=null ){
            throw new Exception("Series is already present");
        }
        WebSeries webSeries = new WebSeries(webSeriesEntryDto.getSeriesName(),webSeriesEntryDto.getAgeLimit(),webSeriesEntryDto.getRating(),webSeriesEntryDto.getSubscriptionType());
        ProductionHouse productionHouse=productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();

        Double existingRating=productionHouse.getRatings();
        int numberOfWebSeries=productionHouse.getWebSeriesList().size();
        Double newAvgRating=((existingRating*(numberOfWebSeries))+webSeries.getRating())/(numberOfWebSeries+1);
        productionHouse.setRatings(newAvgRating);
        productionHouse.getWebSeriesList().add(webSeries);

        webSeries.setProductionHouse(productionHouse);
        WebSeries savedwebseries= webSeriesRepository.save(webSeries);
        productionHouseRepository.save(productionHouse);
        return savedwebseries.getId();
    }

}

package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User user1=new User(user.getId(),user.getName(),user.getAge(),user.getMobNo());
        if(user.getSubscription()!=null){
            user1.setSubscription(user.getSubscription());
        }
        User savedUser =userRepository.save(user1);
        return savedUser.getId();//jkjkj
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        User user=userRepository.findById(userId).get();
        Subscription subscription=user.getSubscription();
        if(subscription==null)
            return 0;
        int ans=0;
        List<WebSeries> webSeriesList= webSeriesRepository.findAll();
        List<WebSeries> basic=new ArrayList<>();
        List<WebSeries> elite=new ArrayList<>();
        List<WebSeries> pro=new ArrayList<>();
        for(WebSeries w:webSeriesList){
            if(w.getAgeLimit()>user.getAge())
                continue;
            if(w.getSubscriptionType()==(SubscriptionType.BASIC)){
                basic.add(w);
            }
        else if (w.getSubscriptionType()==(SubscriptionType.PRO)){
                pro.add(w);
            }
            else if(w.getSubscriptionType()==(SubscriptionType.ELITE)){
                elite.add(w);
            }
        }
        if (user.getSubscription().getSubscriptionType()==(SubscriptionType.BASIC)){
            ans= basic.size();;
        }
        if (user.getSubscription().getSubscriptionType()==(SubscriptionType.PRO)){
            ans= basic.size()+ pro.size();
        }
        if (user.getSubscription().getSubscriptionType() == SubscriptionType.ELITE){
            ans= basic.size()+pro.size()+ elite.size();
        }
        return ans;
    }



}

package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){
        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Optional <Subscription> optionalSubscription= subscriptionRepository.findById(subscriptionEntryDto.getUserId());
        if(!optionalSubscription.isPresent())
            return null;
        Subscription subscription=optionalSubscription.get();
        SubscriptionType subscriptionType=subscription.getSubscriptionType();
        int screens=subscription.getNoOfScreensSubscribed();
        int amt=0;
        if(subscriptionType.toString().equals("BASIC")){
                amt=500 + 200*screens;
        }
        else if(subscriptionType.toString().equals("PRO")){
                amt=800 + 250*screens;
        }
        else if (subscriptionType.toString().equals("ELITE")){
                amt=1000+ 350*screens;
        }
        subscription.setTotalAmountPaid(amt);
        subscriptionRepository.save(subscription);
//        if(subscription.getUser()!=null){
//            User user=subscription.getUser();
//            user.setSubscription(subscription);
//            subscription.setUser(user);
//            subscriptionRepository.save(subscription);
//            userRepository.save(user);
//        }

        return amt;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

//        Optional <User> optionalUser = userRepository.findById(userId);
//        if(!optionalUser.isPresent()){
//            return null;
//        }
        //User user=optionalUser.get();
        Optional <Subscription> optionalSubscription=subscriptionRepository.findById(userId);
        if(!optionalSubscription.isPresent())
            return 0;
        Subscription subscription=optionalSubscription.get();
        SubscriptionType subscriptionType=subscription.getSubscriptionType();
        int screens=subscription.getNoOfScreensSubscribed();

        if(subscriptionType.toString().equals("ELITE"))
            throw new Exception("Already the best Subscription");

        if(subscriptionType.toString().equals("PRO")){
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(200+(100*screens));
            subscriptionRepository.save(subscription);
//            user.setSubscription(subscription);
//            userRepository.save(user);
            return 200+(100*screens);
        }
        if(subscriptionType.toString().equals("BASIC")){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(300+(50*screens));
            subscriptionRepository.save(subscription);
//            user.setSubscription(subscription);
//            userRepository.save(user);
            return 300+(50*screens);
        }

        return null;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        if(subscriptionRepository.count()==0)
            return 0;
        List<Subscription> subscriptionList= subscriptionRepository.findAll();
        if(subscriptionList.isEmpty())
            return 0;
        int ans=0;
        for(Subscription s : subscriptionList){
            ans+= s.getTotalAmountPaid();
        }
        return ans;
    }

}

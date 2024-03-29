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
        Optional<User> optionalUser=userRepository.findById(subscriptionEntryDto.getUserId());
        if(!optionalUser.isPresent())
            return null;
        User user=optionalUser.get();

        Subscription subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setUser(user);

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

        if(subscription.getUser()!=null){
            User user1=subscription.getUser();
            user1.setSubscription(subscription);
            userRepository.save(user1);
        }

        return amt;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        Optional <User> optionalUser = userRepository.findById(userId);
        if(!optionalUser.isPresent()){
            return null;
        }
        User user=optionalUser.get();
        Subscription subscription=user.getSubscription();
        if(subscription==null)
            return 0;

        SubscriptionType subscriptionType=subscription.getSubscriptionType();
        int screens=subscription.getNoOfScreensSubscribed();

        if(subscriptionType.toString().equals("ELITE"))
            throw new Exception("Already the best Subscription");

        if(subscriptionType.toString().equals("PRO")){
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            subscription.setTotalAmountPaid(200+(100*screens));
            subscriptionRepository.save(subscription);
            user.setSubscription(subscription);
            userRepository.save(user);
            return 200+(100*screens);
        }
        if(subscriptionType.toString().equals("BASIC")){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            subscription.setTotalAmountPaid(300+(50*screens));
            subscriptionRepository.save(subscription);
            user.setSubscription(subscription);
            userRepository.save(user);
            return 300+(50*screens);
        }

        return null;
    }

    public Integer calculateTotalRevenueOfHotstar() {

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        int ans=0;

//        if(subscriptionRepository.count()==0)
//            return ans;
        List <Subscription> subscriptionList= subscriptionRepository.findAll();
        if(subscriptionList.isEmpty())
            return ans;
        for(Subscription s : subscriptionList){
            ans+= s.getTotalAmountPaid();
        }
        return ans;
    }

}

package com.appsinventiv.mrappliance;

import com.appsinventiv.mrappliance.Services.SubServiceModel;

public interface AddToCartCallbacks {
    public void addedToCart(SubServiceModel services, int quantity);
    public void deletedFromCart(SubServiceModel services);
    public void quantityUpdate(SubServiceModel services, int quantity);

}

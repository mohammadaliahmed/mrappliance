package com.fixed.fixitservices;

import com.fixed.fixitservices.Services.SubServiceModel;

public interface AddToCartCallbacks {
    public void addedToCart(SubServiceModel services, int quantity);
    public void deletedFromCart(SubServiceModel services);
    public void quantityUpdate(SubServiceModel services, int quantity);

}

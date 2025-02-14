package lk.jiat.orterclothing.ui.home;

// HomeViewModel.java
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import lk.jiat.orterclothing.model.Product;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> productList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Product>> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> products) {
        productList.postValue(products);
    }
}
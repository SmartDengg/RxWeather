package com.joker.rxweather.presenter;

import com.joker.rxweather.common.rx.rxAndroid.SimpleObserver;
import com.joker.rxweather.exception.Exceptions;
import com.joker.rxweather.model.entity.AddressEntity;
import com.joker.rxweather.model.entity.SearchEntity;
import com.joker.rxweather.views.SearchView;
import com.rxweather.domain.usercase.SearchUseCase;
import com.rxweather.domain.usercase.UseCase;
import rx.Observable;

/**
 * Created by Administrator on 2015/11/4.
 */
public class SearchPresenterImp implements SearchPresenter<SearchView<Observable<SearchEntity>>> {

  private SearchView<Observable<SearchEntity>> searchView;
  private UseCase<SearchEntity> searchCase;

  private AddressEntity addressEntity = new AddressEntity();

  public SearchPresenterImp() {
  }

  @Override public void attachView(SearchView<Observable<SearchEntity>> view) {
    this.searchView = view;
    this.searchCase = new SearchUseCase(addressEntity);
  }

  @Override public void search(String cityName) {

    SearchPresenterImp.this.showLoading();

    this.addressEntity.city = cityName;
    this.searchCase.subscribe(new SearchSubscriber());
  }

  private void showLoading() {
    if (!searchView.isContent()) {
      this.searchView.showLoading();
    }
  }

  private void showContent(SearchEntity searchEntity) {
    this.searchView.showSearchResult(Observable.just(searchEntity).cache());
  }

  private void showError(int messageId) {
    this.searchView.showError(messageId);
  }

  public final class SearchSubscriber extends SimpleObserver<SearchEntity> {

    @Override public void onCompleted() {
      SearchPresenterImp.this.searchView.showContent();
    }

    @Override public void onError(Throwable e) {
      SearchPresenterImp.this.showError(Exceptions.propagate(e));
    }

    @Override public void onNext(SearchEntity searchEntity) {
      SearchPresenterImp.this.showContent(searchEntity);
    }
  }

  @Override public void detachView() {
    this.searchCase.unsubscribe();
  }
}

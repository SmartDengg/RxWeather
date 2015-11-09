package com.joker.rxweather.presenter;

import com.fernandocejas.frodo.annotation.RxLogSubscriber;
import com.joker.rxweather.exception.ErrorHanding;
import com.joker.rxweather.model.entities.AddressEntity;
import com.joker.rxweather.model.entities.SearchEntity;
import com.joker.rxweather.views.SearchView;
import com.rxweather.domain.usercase.SearchUseCase;
import com.rxweather.domain.usercase.UseCase;
import rx.Observable;
import rx.Subscriber;

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

  @RxLogSubscriber public final class SearchSubscriber extends Subscriber<SearchEntity> {

    @Override public void onCompleted() {
      SearchPresenterImp.this.searchView.showContent();
    }

    @Override public void onError(Throwable e) {
      SearchPresenterImp.this.showError(ErrorHanding.propagate(e));
    }

    @Override public void onNext(SearchEntity searchEntity) {
      SearchPresenterImp.this.showContent(searchEntity);
    }
  }

  @Override public void detachView() {
    this.searchCase.unsubscribe();
  }
}

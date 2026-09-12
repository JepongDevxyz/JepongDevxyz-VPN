package com.google.android.gms.tasks;
public class Task<T> {
 private final T result; private final Exception error;
 public Task(T r){result=r;error=null;} public Task(Exception e){result=null;error=e;}
 public boolean isSuccessful(){return error==null;} public T getResult(){return result;} public Exception getException(){return error;}
 public Task<T> addOnCompleteListener(OnCompleteListener<T> l){if(l!=null)l.onComplete(this);return this;}
 public Task<T> addOnSuccessListener(OnSuccessListener<? super T> l){if(error==null&&l!=null)l.onSuccess(result);return this;}
 public Task<T> addOnFailureListener(OnFailureListener l){if(error!=null&&l!=null)l.onFailure(error);return this;}
}

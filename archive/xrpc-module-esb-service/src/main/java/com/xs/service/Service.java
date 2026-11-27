package com.xs.service;

public interface Service<K> {

   void execute (K k) throws Exception;

//   boolean support(String trancode);
}

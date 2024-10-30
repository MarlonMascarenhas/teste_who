package com.teste.who.converter;

import java.util.Set;

public interface CollectionConverter<S, T> {
    T convert(S source);
}

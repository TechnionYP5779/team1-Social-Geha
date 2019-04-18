package com.example.ofir.social_geha.Firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

class QueryBuilder {
    private Query query;

    QueryBuilder(CollectionReference reference) {
        query = reference;
    }

    QueryBuilder addWhereEquals(String field, Object value) {
        if (value != null)
            query = query.whereEqualTo(field, value);
        return this;
    }

    QueryBuilder addLowerBound(String field, Object value) {
        if (value != null)
            query = query.whereGreaterThanOrEqualTo(field, value);
        return this;
    }

    QueryBuilder addUpperBound(String field, Object value) {
        if (value != null)
            query = query.whereLessThanOrEqualTo(field, value);
        return this;
    }

    Query build() {
        return query;
    }
}

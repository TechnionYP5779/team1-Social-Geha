package com.example.ofir.social_geha.Firebase;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

/***
 * This is a Builder class to construct a Firebase Query
 * In order to avoid verbose checks to avoid null values this class exists
 * Note there are some limitations on the query format
 * For more information visit https://firebase.google.com/docs/firestore/query-data/queries
 */
class QueryBuilder {
    private Query query;

    /***
     * This returns an initial query without additional clauses
     * @param reference The initial database
     */
    QueryBuilder(CollectionReference reference) {
        query = reference;
    }

    /***
     * Adds an additional where clause to the existing query being built
     * @param field The name of the field
     * @param value The value this field equals to (Can be null, in which case nothing would happen)
     * @return The same instance with the additional clause
     */
    QueryBuilder addWhereEquals(String field, Object value) {
        if (value != null)
            query = query.whereEqualTo(field, value);
        return this;
    }

    /***
     * Adds a range clause to the query (can be one in each query)
     * @param field The name of the field
     * @param value The value this field is greater than or equal to (Can be null, in which case nothing would happen)
     * @return The same instance with the additional clause
     */
    QueryBuilder addLowerBound(String field, Object value) {
        if (value != null)
            query = query.whereGreaterThanOrEqualTo(field, value);
        return this;
    }

    /***
     * Adds a range clause to the query (can be one in each query)
     * @param field The name of the field
     * @param value The value this field is less than or equal to (Can be null, in which case nothing would happen)
     * @return The same instance with the additional clause
     */
    QueryBuilder addUpperBound(String field, Object value) {
        if (value != null)
            query = query.whereLessThanOrEqualTo(field, value);
        return this;
    }

    /***
     * The builder function
     * @return The query built with all the clauses
     */
    Query build() {
        return query;
    }
}

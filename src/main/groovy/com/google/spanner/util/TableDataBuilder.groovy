package com.google.spanner.util

import com.google.cloud.spanner.Mutation
import com.google.cloud.spanner.Value
import com.google.cloud.spanner.ValueBinder
import com.google.cloud.spanner.Mutation.WriteBuilder

class TableDataBuilder {

     static List<Mutation> createMutationList(String tableName, List<Map> insertDataList) {
        def mutations = new ArrayList<Mutation>()
        for (Map insertData : insertDataList) {
            int size = insertData.size()
            mutations.add(createMutations(tableName, size, insertData))
        }

        return mutations
    }

    static Mutation createMutations(String tableName, int size, Map insertData) {
        Mutation mutation
        String[] keys = insertData.keySet()
        switch (size) {
            case 1:
                mutation = create1Mutation(tableName, insertData, keys)
                break
            case 2:
                mutation = create2Mutation(tableName, insertData, keys)
                break
            case 3:
                mutation = create3Mutation(tableName, insertData, keys)
                break
            case 4:
                mutation = create4Mutation(tableName, insertData, keys)
                break
            case 5:
                mutation = create5Mutation(tableName, insertData, keys)
                break
            case 6:
                mutation = create6Mutation(tableName, insertData, keys)
                break
            case 7:
                mutation = create7Mutation(tableName, insertData, keys)
                break
            case 8:
                mutation = create8Mutation(tableName, insertData, keys)
                break
            case 9:
                mutation = create9Mutation(tableName, insertData, keys)
                break
            case 10:
                mutation = create10Mutation(tableName, insertData, keys)
                break
            case 11:
                mutation = create11Mutation(tableName, insertData, keys)
                break
            default:
                mutation = null
                break
        }
        return mutation
    }

    static Mutation create1Mutation(String tableName, Map insertData, String... keys) {

        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .build()
    }

    static Mutation create2Mutation(String tableName, Map insertData, String... keys) {
        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .set(keys[1]).to(insertData?.get(keys[1]) )
                .build()
    }

    static Mutation create3Mutation(String tableName, Map insertData, String... keys) {
        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .set(keys[1]).to(insertData?.get(keys[1]) )
                .set(keys[2]).to(insertData?.get(keys[2]) )
                .build()
    }

    static Mutation create4Mutation(String tableName, Map insertData, String... keys) {
        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .set(keys[1]).to(insertData?.get(keys[1]) )
                .set(keys[2]).to(insertData?.get(keys[2]) )
                .set(keys[3]).to(insertData?.get(keys[3]) )
                .build()
    }

    static Mutation create5Mutation(String tableName, Map insertData, String... keys) {
        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .set(keys[1]).to(insertData?.get(keys[1]) )
                .set(keys[2]).to(insertData?.get(keys[2]) )
                .set(keys[3]).to(insertData?.get(keys[3]) )
                .set(keys[4]).to(insertData?.get(keys[4]) )
                .build()
    }

    static Mutation create6Mutation(String tableName, Map insertData, String... keys) {
        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .set(keys[1]).to(insertData?.get(keys[1]) )
                .set(keys[2]).to(insertData?.get(keys[2]) )
                .set(keys[3]).to(insertData?.get(keys[3]) )
                .set(keys[4]).to(insertData?.get(keys[4]) )
                .set(keys[5]).to(insertData?.get(keys[5]) )
                .build()
    }

    static Mutation create7Mutation(String tableName, Map insertData, String... keys) {
        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .set(keys[1]).to(insertData?.get(keys[1]) )
                .set(keys[2]).to(insertData?.get(keys[2]) )
                .set(keys[3]).to(insertData?.get(keys[3]) )
                .set(keys[4]).to(insertData?.get(keys[4]) )
                .set(keys[5]).to(insertData?.get(keys[5]) )
                .set(keys[6]).to(insertData?.get(keys[6]) )
                .build()
    }

    static Mutation create8Mutation(String tableName, Map insertData, String... keys) {
        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .set(keys[1]).to(insertData?.get(keys[1]) )
                .set(keys[2]).to(insertData?.get(keys[2]) )
                .set(keys[3]).to(insertData?.get(keys[3]) )
                .set(keys[4]).to(insertData?.get(keys[4]) )
                .set(keys[5]).to(insertData?.get(keys[5]) )
                .set(keys[6]).to(insertData?.get(keys[6]) )
                .set(keys[7]).to(insertData?.get(keys[7]) )
                .build()
    }

    static Mutation create9Mutation(String tableName, Map insertData, String... keys) {
        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .set(keys[1]).to(insertData?.get(keys[1]) )
                .set(keys[2]).to(insertData?.get(keys[2]) )
                .set(keys[3]).to(insertData?.get(keys[3]) )
                .set(keys[4]).to(insertData?.get(keys[4]) )
                .set(keys[5]).to(insertData?.get(keys[5]) )
                .set(keys[6]).to(insertData?.get(keys[6]) )
                .set(keys[7]).to(insertData?.get(keys[7]) )
                .set(keys[8]).to(insertData?.get(keys[8]) )
                .build()
    }

    static Mutation create10Mutation(String tableName, Map insertData, String... keys) {
        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .set(keys[1]).to(insertData?.get(keys[1]) )
                .set(keys[2]).to(insertData?.get(keys[2]) )
                .set(keys[3]).to(insertData?.get(keys[3]) )
                .set(keys[4]).to(insertData?.get(keys[4]) )
                .set(keys[5]).to(insertData?.get(keys[5]) )
                .set(keys[6]).to(insertData?.get(keys[6]) )
                .set(keys[7]).to(insertData?.get(keys[7]) )
                .set(keys[8]).to(insertData?.get(keys[8]) )
                .set(keys[9]).to(insertData?.get(keys[9]) )
                .build()
    }

    static Mutation create11Mutation(String tableName, Map insertData, String... keys) {
        return Mutation.newInsertOrUpdateBuilder(tableName)
                .set(keys[0]).to(insertData?.get(keys[0]) )
                .set(keys[1]).to(insertData?.get(keys[1]) )
                .set(keys[2]).to(insertData?.get(keys[2]) )
                .set(keys[3]).to(insertData?.get(keys[3]) )
                .set(keys[4]).to(insertData?.get(keys[4]) )
                .set(keys[5]).to(insertData?.get(keys[5]) )
                .set(keys[6]).to(insertData?.get(keys[6]) )
                .set(keys[7]).to(insertData?.get(keys[7]) )
                .set(keys[8]).to(insertData?.get(keys[8]) )
                .set(keys[9]).to(insertData?.get(keys[9]) )
                .set(keys[10]).to(insertData?.get(keys[10]) )
                .build()
    }
}

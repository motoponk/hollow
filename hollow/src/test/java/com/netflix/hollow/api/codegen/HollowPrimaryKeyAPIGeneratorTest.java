/*
 *  Copyright 2017 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package com.netflix.hollow.api.codegen;

import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import org.junit.Test;

public class HollowPrimaryKeyAPIGeneratorTest extends AbstractHollowAPIGeneratorTest {

    @Test
    public void test() throws Exception {
        String apiClassName = "PrimaryKeyIndexTestAPI";
        String packageName = "codegen.primarykey";
        runGenerator(apiClassName, packageName, Movie.class,
                builder -> builder.reservePrimaryKeyIndexForTypeWithPrimaryKey(true));
    }

    @Test
    public void testWithPostfix() throws Exception {
        String apiClassName = "PrimaryKeyIndexTestAPI";
        String packageName = "codegen.primarykey";
        runGenerator(apiClassName, packageName, Movie.class,
                builder -> builder.withClassPostfix("Generated").withPackageGrouping());
    }

    @HollowPrimaryKey(fields = { "id", "hasSubtitles", "actor", "role.id!", "role.rank" })
    static class Movie {
        int id;

        Boolean hasSubtitles;

        Actor actor;
        Role role;
    }

    static class Actor {
        String name;
    }

    static class Role {
        Integer id;

        Long rank;

        String name;
    }
}

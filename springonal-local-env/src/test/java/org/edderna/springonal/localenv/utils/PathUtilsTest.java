package org.edderna.springonal.localenv.utils;

/*-
 * #%L
 * springonal
 * %%
 * Copyright (C) 2025 Eduardo Daniel Hernandez
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PathUtilsTest {

    @Test
    void shouldReturnFileNameWithLongPath() {
        var longPath = "/path/to/my/file1.f";

        var name = PathUtils.getFilename(longPath);

        assertThat(name).isEqualTo("file1.f");
    }

    @Test
    void shouldReturnFileNameWithOnlyName() {
        var longPath = "file2.f";

        var name = PathUtils.getFilename(longPath);

        assertThat(name).isEqualTo("file2.f");
    }
}

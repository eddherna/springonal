package org.edderna.springonal.annotations.domain;

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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a class as a Domain Entity in the domain layer.
 * 
 * <p>This annotation identifies classes that represent domain entities in Domain-Driven Design (DDD).
 * Domain entities are objects that have a distinct identity and lifecycle within the domain model.
 * Unlike value objects, entities are defined by their identity rather than their attributes.</p>
 * 
 * <p>Classes annotated with {@code @DomainEntity} typically contain business logic
 * and maintain state that is important to the domain model.</p>
 * 
 * <p>Example usage:</p>
 * <pre>{@code
 * @DomainEntity
 * public class Customer {
 *     private CustomerId id;
 *     // Entity implementation
 * }
 * }</pre>
 * 
 * @author Eduardo Daniel Hernandez
 * @since 1.0.0
 * @see AggregateRoot
 * @see ValueObject
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DomainEntity {
}

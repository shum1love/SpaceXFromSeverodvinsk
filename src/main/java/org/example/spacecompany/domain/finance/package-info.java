/**
 * Деньги: каждая копейка с историей.
 *
 * <p>{@link org.example.spacecompany.domain.finance.Transaction} неизменяема:
 * сумма всегда положительная, направление задаёт тип (доход/расход).
 * Деньги — только {@code BigDecimal}, никогда {@code double} (иначе копейки
 * потеряются в ошибках округления).
 */
package org.example.spacecompany.domain.finance;

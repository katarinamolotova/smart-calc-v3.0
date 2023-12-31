package edu.school21.model;

import edu.school21.enums.CreditType;
import edu.school21.enums.TermType;
import java.util.function.Function;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public class CreditCalcModel {

  private static final Double MAX_PERCENT = 100.;
  private static final Double SCALE = 100.;
  private static final Integer MONTHS_OF_YEAR = 12;

  private ArrayList<Double> everyMothPay;
  private double totalPayment;
  private double overpay;

  public void calculate(
      final CreditType type,
      final double sum,
      final int amountOfMonth,
      final TermType termType,
      final double percent
  ) {
    everyMothPay(type, sum, amountOfMonth, termType, percent);
    totalPayment();
    overpay(sum);
  }

  private void everyMothPay(
      final CreditType type,
      final double sum,
      final int amountOfMonth,
      final TermType termType,
      final double percent
  ) {
    everyMothPay = new ArrayList<>();
    double dynamicSum = sum;
    final int period =
        (termType == TermType.MONTH) ? amountOfMonth : amountOfMonth * MONTHS_OF_YEAR;
    final double newPercent = percent / MAX_PERCENT / MONTHS_OF_YEAR;

    for (int i = 0; i < period; i++) {
      final double result;
      if (type == CreditType.ANNUITY) {
        result = sum * (newPercent / (1. - Math.pow(1. + newPercent, (-1. * period))));
      } else {
        result = sum / period + dynamicSum * newPercent;
      }
      everyMothPay.add(Math.ceil(result * SCALE) / SCALE);
      dynamicSum -= everyMothPay.get(i);
    }
  }

  private void totalPayment() {
    totalPayment = everyMothPay.stream().mapToDouble(i -> i).sum();
    totalPayment = Math.ceil(totalPayment * SCALE) / SCALE;
  }

  private void overpay(final double sum) {
    overpay = Math.ceil((totalPayment - sum) * SCALE) / SCALE;
  }
}

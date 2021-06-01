package com.omniscien.lsmetrix.test.dev;

public class TestcalculateLevenString {

	public TestcalculateLevenString() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		com.omniscien.lsmetric.controller.Metrics mt = new com.omniscien.lsmetric.controller.Metrics();
		int result = mt.calculateLevenString("Los Times estaban cambiando .", "Los tiempos estaban cambiando .", true);
		System.out.println("result: "+result);

	}

}

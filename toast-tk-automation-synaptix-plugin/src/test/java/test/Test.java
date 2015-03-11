//package test;
//
//public class Test {
//
//	static class A {
//		A() {
//			//new B(0L);
//		}
//		
//		A(Long i) {
//			
//		}
//	}
//	
//	class B extends A {
//		B(Long i) {
//			new B( i/ Long.compare(i ,i));
//			System.out.println("Win");
//		}
//	}
//
//	public static void main(String[] args) {
//		new Test().postMain(args);
//	}
//	
//	public void postMain(String[] args) {
//		System.out.println("started");
//		B b = new B(1L);
//		System.out.println("ended");
//	}
//}
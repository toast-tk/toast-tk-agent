//package fr.synaptix.aop;
//
//import java.lang.annotation.Annotation;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//
//import com.synaptix.redpepper.swing.agent.InspectionManager;
//
///**
// * Created by Sallah Kokaina on 17/11/2014.
// */
//
////@Aspect
//public class InspectionAspect {
//
//    //@Pointcut("call(*.new(..)) && !within(InspectProviderAspect) && within(@fr.synaptix.agent.shared.InspectionEntryPoint *)")
//    public void myTraceCall() {
//    }
//
//    //@Around("fr.synaptix.agent.aspect.InspectProviderAspect.myTraceCall()")
//    public Object myTrace(ProceedingJoinPoint joinPoint) throws Throwable
//    {
//        System.out.println("-> Profiling - " +joinPoint.getStaticPart());
//
//        Object retVal = null;
//        try
//        {
//            retVal = joinPoint.proceed();
//        }
//        finally
//        {
//            if(retVal.getClass().getAnnotations() != null && InspectionManager.getInstance().isValidInstance(retVal)){
//                for (Annotation annotation : retVal.getClass().getAnnotations()) {
////                    if(annotation instanceof InspectionEntryPoint){
////                        System.out.println("-> new instance of "+ retVal.getClass() + " registered for Gui Inspection..");
////                        InspectionManager.getInstance().addContainer(retVal);
////                    }
//                }
//            }
//
//        }
//        return retVal;
//    }
//}

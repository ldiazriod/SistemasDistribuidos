package HelloApp;


/**
* HelloApp/IHelloWorldPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from HelloWorld.idl
* lunes 28 de febrero de 2022 12H35' CET
*/

public abstract class IHelloWorldPOA extends org.omg.PortableServer.Servant
 implements HelloApp.IHelloWorldOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("HelloWorld", new java.lang.Integer (0));
    _methods.put ("shutdown", new java.lang.Integer (1));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // HelloApp/IHelloWorld/HelloWorld
       {
         String $result = null;
         $result = this.HelloWorld ();
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // HelloApp/IHelloWorld/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:HelloApp/IHelloWorld:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public IHelloWorld _this() 
  {
    return IHelloWorldHelper.narrow(
    super._this_object());
  }

  public IHelloWorld _this(org.omg.CORBA.ORB orb) 
  {
    return IHelloWorldHelper.narrow(
    super._this_object(orb));
  }


} // class IHelloWorldPOA

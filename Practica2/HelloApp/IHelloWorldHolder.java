package HelloApp;

/**
* HelloApp/IHelloWorldHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from HelloWorld.idl
* lunes 28 de febrero de 2022 12H35' CET
*/

public final class IHelloWorldHolder implements org.omg.CORBA.portable.Streamable
{
  public HelloApp.IHelloWorld value = null;

  public IHelloWorldHolder ()
  {
  }

  public IHelloWorldHolder (HelloApp.IHelloWorld initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = HelloApp.IHelloWorldHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    HelloApp.IHelloWorldHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return HelloApp.IHelloWorldHelper.type ();
  }

}

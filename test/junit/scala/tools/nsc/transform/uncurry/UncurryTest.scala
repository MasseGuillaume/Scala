package scala.tools.nsc.transform.uncurry


import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

import scala.tools.nsc.io.{VirtualDirectory, AbstractFile}
import scala.reflect.internal.util.{BatchSourceFile, AbstractFileClassLoader}
import scala.tools.testing.BytecodeTesting._

@RunWith(classOf[JUnit4])
class UncurryTest {

  @Test
  def testVarArgs(): Unit = {

    val target = new VirtualDirectory("(memory)", None)
    val classLoader = new AbstractFileClassLoader(target, this.getClass.getClassLoader)
    val compiler = newCompiler()
    compiler.global.settings.outputDirs.setSingleOutput(target)

    val code =
      """|object VarArgs {
         |  @annotation.varargs
         |  def call(args: Int*): Int = args.size
         |}""".stripMargin

    new compiler.global.Run().compileSources(List(new BatchSourceFile("(inline)", code)))

    val varargsClass = classLoader.loadClass("VarArgs")
    val obtained = varargsClass.getDeclaredMethods.mkString
    val expected = 
      """|public static int VarArgs.call(int[])
         |public static int VarArgs.call(scala.collection.Seq)""".stripMargin

    println(obtained)
    assertEquals(expected, obtained)
  }
}

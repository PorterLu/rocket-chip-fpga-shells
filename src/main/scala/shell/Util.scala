package sifive.fpgashells.shell

import chisel3._
import chisel3.experimental.{Analog, attach}
import chisel3.util.HasBlackBoxInline

import org.chipsalliance.cde.config.Field

case object FPGAFrequencyKey extends Field[Double](100.0)

class AnalogToUInt(w: Int = 1) extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val a = Analog(w.W)
    val b = Output(UInt(w.W))
  })

  override def desiredName = s"AnalogToUInt_${w.toString}"

  setInline(s"AnalogToUInt_${w.toString}.v",
    s"""module AnalogToUInt_${w.toString} (a, b);
       |  inout [${w - 1}:0] a;
       |  output [${w - 1}:0] b;
       |  assign b = a;
       |endmodule
       |""".stripMargin)
}

object AnalogToUInt {
  def apply(a: Analog): UInt = {
    val a2b = Module(new AnalogToUInt(w = a.getWidth))
    attach(a, a2b.io.a)
    a2b.io.b
  }
}

class UIntToAnalog(w: Int = 1) extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val a = Analog(w.W)
    val b = Input(UInt(w.W))
    val b_en = Input(Bool())
  })

  override def desiredName = s"UIntToAnalog_${w.toString}"

  require(w >= 1)
  setInline(s"UIntToAnalog_${w.toString}.v",
    s"""module UIntToAnalog_${w.toString} (a, b, b_en);
       |  inout [${w - 1}:0] a;
       |  input [${w - 1}:0] b;
       |  input b_en;
       |  assign a = b_en ? b : $w'b${"z"*w};
       |endmodule
       |""".stripMargin)
}

object UIntToAnalog {
  def apply(b: UInt, a: Analog, b_en: Bool): Unit = {
    val a2b = Module(new UIntToAnalog(w = a.getWidth))
    attach(a, a2b.io.a)
    a2b.io.b := b
    a2b.io.b_en := b_en
  }
}

/*
   Copyright 2016 SiFive, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

/* Generated By:JavaCC: Do not edit this line. ParameterGUIParserTokenManager.java */
package Command.JavaCC;
import java.io.StringReader;
import java.util.*;

public class ParameterGUIParserTokenManager implements ParameterGUIParserConstants
{
  public static  java.io.PrintStream debugStream = System.out;
  public static  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private static final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      default :
         return -1;
   }
}
private static final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
static private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
static private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
static private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 44:
         return jjStopAtPos(0, 22);
      case 59:
         return jjStopAtPos(0, 21);
      case 91:
         return jjStartNfaWithStates_0(0, 23, 94);
      case 93:
         return jjStopAtPos(0, 24);
      default :
         return jjMoveNfa_0(18, 0);
   }
}
static private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
static private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
static private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
static private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
static private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
static private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 94;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 18:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 3)
                        kind = 3;
                     jjCheckNAddStates(0, 12);
                  }
                  else if ((0x280000000000L & l) != 0L)
                     jjCheckNAddStates(13, 17);
                  else if ((0x100000200L & l) != 0L)
                  {
                     if (kind > 5)
                        kind = 5;
                     jjCheckNAdd(0);
                  }
                  else if (curChar == 34)
                     jjCheckNAddStates(18, 27);
                  else if (curChar == 47)
                  {
                     if (kind > 12)
                        kind = 12;
                     jjCheckNAddStates(28, 32);
                  }
                  if (curChar == 45)
                  {
                     if (kind > 10)
                        kind = 10;
                     jjCheckNAddStates(33, 38);
                  }
                  break;
               case 94:
               case 19:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddTwoStates(19, 20);
                  break;
               case 0:
                  if ((0x100000200L & l) == 0L)
                     break;
                  if (kind > 5)
                     kind = 5;
                  jjCheckNAdd(0);
                  break;
               case 1:
                  if (curChar != 47)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(28, 32);
                  break;
               case 2:
                  if (curChar != 34)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAdd(2);
                  break;
               case 3:
                  if (curChar != 47)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(39, 44);
                  break;
               case 4:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAddTwoStates(5, 8);
                  break;
               case 5:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(45, 49);
                  break;
               case 6:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(5);
                  break;
               case 7:
                  if (curChar != 45)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(45, 49);
                  break;
               case 8:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(50, 55);
                  break;
               case 9:
                  if (curChar != 46)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(56, 60);
                  break;
               case 10:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(11);
                  break;
               case 11:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(56, 60);
                  break;
               case 12:
                  if (curChar != 45)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(56, 60);
                  break;
               case 13:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAddTwoStates(8, 11);
                  break;
               case 14:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(61, 69);
                  break;
               case 15:
                  if (curChar != 45)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(61, 69);
                  break;
               case 16:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(70, 78);
                  break;
               case 17:
                  if (curChar != 45)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(70, 78);
                  break;
               case 21:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAddStates(13, 17);
                  break;
               case 22:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAdd(22);
                  break;
               case 23:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(79, 82);
                  break;
               case 24:
                  if (curChar != 45)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(83, 86);
                  break;
               case 25:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(26);
                  break;
               case 26:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(83, 86);
                  break;
               case 27:
                  if (curChar != 47)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(83, 86);
                  break;
               case 28:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAddTwoStates(23, 26);
                  break;
               case 29:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(87, 92);
                  break;
               case 30:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(30, 31);
                  break;
               case 31:
                  if (curChar == 58)
                     jjCheckNAddTwoStates(32, 33);
                  break;
               case 32:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(33);
                  break;
               case 33:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 17)
                     kind = 17;
                  jjCheckNAdd(33);
                  break;
               case 34:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(34, 35);
                  break;
               case 35:
                  if (curChar == 58)
                     jjCheckNAddTwoStates(36, 37);
                  break;
               case 36:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(37);
                  break;
               case 37:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(37, 38);
                  break;
               case 38:
                  if (curChar == 58)
                     jjCheckNAddTwoStates(39, 40);
                  break;
               case 39:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(40);
                  break;
               case 40:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 18)
                     kind = 18;
                  jjCheckNAdd(40);
                  break;
               case 41:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddStates(93, 96);
                  break;
               case 42:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(41);
                  break;
               case 44:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(45);
                  break;
               case 45:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddTwoStates(44, 45);
                  break;
               case 46:
                  if (curChar != 46)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddStates(97, 99);
                  break;
               case 47:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddTwoStates(47, 43);
                  break;
               case 48:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddTwoStates(48, 43);
                  break;
               case 49:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 19)
                     kind = 19;
                  jjCheckNAddStates(100, 104);
                  break;
               case 50:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 3)
                     kind = 3;
                  jjCheckNAddStates(0, 12);
                  break;
               case 51:
                  if (curChar != 45)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(33, 38);
                  break;
               case 52:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(53);
                  break;
               case 53:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(105, 108);
                  break;
               case 54:
                  if (curChar != 45)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(105, 108);
                  break;
               case 55:
                  if (curChar != 47)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(105, 108);
                  break;
               case 56:
                  if (curChar == 45)
                     jjCheckNAddTwoStates(56, 71);
                  break;
               case 58:
                  if (curChar != 34)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAdd(58);
                  break;
               case 60:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAddTwoStates(61, 65);
                  break;
               case 61:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(109, 113);
                  break;
               case 62:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(114, 122);
                  break;
               case 63:
                  if (curChar != 46)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(123, 127);
                  break;
               case 64:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAddStates(128, 130);
                  break;
               case 65:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(131, 136);
                  break;
               case 66:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(137, 148);
                  break;
               case 67:
                  if (curChar != 45)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(114, 122);
                  break;
               case 68:
                  if (curChar != 45)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(137, 148);
                  break;
               case 69:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(123, 127);
                  break;
               case 71:
                  if (curChar == 58)
                     jjstateSet[jjnewStateCnt++] = 70;
                  break;
               case 72:
                  if (curChar != 45)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(149, 153);
                  break;
               case 73:
                  if (curChar == 34)
                     jjCheckNAddStates(18, 27);
                  break;
               case 74:
                  if (curChar == 34)
                     jjCheckNAddTwoStates(74, 56);
                  break;
               case 75:
                  if (curChar == 34)
                     jjCheckNAddTwoStates(75, 1);
                  break;
               case 76:
                  if (curChar == 45)
                     jjCheckNAddStates(154, 159);
                  break;
               case 77:
                  if ((0xfc00fffaffffd9ffL & l) != 0L)
                     jjCheckNAddStates(154, 159);
                  break;
               case 78:
                  if ((0x100000200L & l) != 0L)
                     jjCheckNAddStates(154, 159);
                  break;
               case 79:
                  if (curChar != 34)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjCheckNAdd(79);
                  break;
               case 80:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAddTwoStates(81, 87);
                  break;
               case 81:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(154, 159);
                  break;
               case 82:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(160, 169);
                  break;
               case 84:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAdd(85);
                  break;
               case 85:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(170, 175);
                  break;
               case 86:
                  if ((0x280000000000L & l) != 0L)
                     jjCheckNAddStates(176, 178);
                  break;
               case 87:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(179, 186);
                  break;
               case 88:
                  if (curChar == 46)
                     jjCheckNAddStates(187, 194);
                  break;
               case 89:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(195, 201);
                  break;
               case 90:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(202, 212);
                  break;
               case 91:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(213, 223);
                  break;
               case 92:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddStates(224, 235);
                  break;
               case 93:
                  if (curChar != 34)
                     break;
                  if (kind > 16)
                     kind = 16;
                  jjCheckNAddStates(236, 242);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 18:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 10)
                        kind = 10;
                     jjCheckNAddStates(33, 38);
                  }
                  else if (curChar == 91)
                     jjAddStates(243, 244);
                  break;
               case 94:
               case 20:
                  if (curChar == 93 && kind > 20)
                     kind = 20;
                  break;
               case 7:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(45, 49);
                  break;
               case 12:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(56, 60);
                  break;
               case 15:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(61, 69);
                  break;
               case 17:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 12)
                     kind = 12;
                  jjCheckNAddStates(70, 78);
                  break;
               case 24:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(83, 86);
                  break;
               case 27:
                  if ((0x1000000010000000L & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(83, 86);
                  break;
               case 43:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(245, 246);
                  break;
               case 51:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(33, 38);
                  break;
               case 54:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(105, 108);
                  break;
               case 55:
                  if ((0x1000000010000000L & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(105, 108);
                  break;
               case 56:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                     jjCheckNAddTwoStates(56, 71);
                  break;
               case 57:
               case 59:
                  if (curChar != 92)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(109, 113);
                  break;
               case 67:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(114, 122);
                  break;
               case 68:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 11)
                     kind = 11;
                  jjCheckNAddStates(137, 148);
                  break;
               case 70:
                  if (curChar == 92)
                     jjstateSet[jjnewStateCnt++] = 57;
                  break;
               case 72:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 10)
                     kind = 10;
                  jjCheckNAddStates(149, 153);
                  break;
               case 76:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                     jjCheckNAddStates(154, 159);
                  break;
               case 77:
                  if ((0xf8000001f8000001L & l) != 0L)
                     jjCheckNAddStates(154, 159);
                  break;
               case 83:
                  if ((0x2000000020L & l) != 0L)
                     jjAddStates(247, 248);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 77:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjAddStates(154, 159);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 94 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   22, 24, 27, 30, 31, 34, 35, 42, 43, 46, 49, 28, 29, 22, 23, 30, 
   34, 41, 74, 56, 75, 1, 76, 77, 78, 80, 82, 93, 2, 3, 4, 16, 
   17, 52, 53, 55, 56, 71, 72, 1, 2, 3, 4, 16, 17, 6, 5, 7, 
   2, 3, 9, 2, 3, 13, 14, 15, 10, 11, 12, 2, 3, 9, 10, 11, 
   12, 2, 3, 13, 14, 15, 6, 5, 7, 9, 2, 3, 13, 14, 15, 24, 
   27, 28, 29, 24, 25, 26, 27, 24, 25, 26, 27, 28, 29, 42, 43, 46, 
   49, 47, 48, 43, 42, 47, 43, 46, 49, 54, 52, 53, 55, 58, 59, 60, 
   62, 67, 63, 58, 59, 60, 62, 67, 64, 66, 68, 58, 59, 64, 66, 68, 
   61, 65, 69, 63, 58, 59, 64, 66, 68, 63, 58, 59, 60, 62, 67, 64, 
   66, 68, 64, 66, 68, 54, 52, 53, 55, 72, 76, 77, 78, 79, 80, 82, 
   76, 77, 78, 83, 79, 80, 82, 88, 80, 91, 76, 77, 78, 79, 86, 92, 
   81, 87, 85, 76, 77, 78, 83, 79, 88, 80, 91, 76, 77, 78, 89, 83, 
   79, 80, 91, 76, 77, 78, 83, 79, 80, 90, 76, 77, 78, 83, 79, 80, 
   82, 90, 88, 80, 91, 76, 77, 78, 83, 79, 80, 82, 91, 88, 80, 91, 
   76, 77, 78, 83, 79, 86, 92, 80, 82, 88, 80, 91, 76, 77, 78, 79, 
   80, 82, 93, 19, 20, 44, 45, 84, 85, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, null, null, null, null, "\73", "\54", "\133", "\135", };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x1ff1c29L, 
};
static final long[] jjtoSkip = {
   0x6L, 
};
static protected SimpleCharStream input_stream;
static private final int[] jjrounds = new int[94];
static private final int[] jjstateSet = new int[188];
static protected char curChar;
public ParameterGUIParserTokenManager(SimpleCharStream stream)
{
   if (input_stream != null)
      throw new TokenMgrError("ERROR: Second call to constructor of static lexer. You must use ReInit() to initialize the static variables.", TokenMgrError.STATIC_LEXER_ERROR);
   input_stream = stream;
}
public ParameterGUIParserTokenManager(SimpleCharStream stream, int lexState)
{
   this(stream);
   SwitchTo(lexState);
}
static public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
static private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 94; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
static public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
static public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

static protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

static int curLexState = 0;
static int defaultLexState = 0;
static int jjnewStateCnt;
static int jjround;
static int jjmatchedPos;
static int jjmatchedKind;

public static Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 13 && (0x2400L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         return matchedToken;
      }
      else
      {
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}

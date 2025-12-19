/*
 * ORIGINAL COPYRIGHT:
 * Copyright (c) 2019-2023 AlphaWallet
 * Licensed under the MIT License (MIT).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * ----------------------------------------------------------------
 * SOURCE:
 * Derived from: https://github.com/AlphaWallet/alpha-wallet-android
 *
 * ----------------------------------------------------------------
 * MODIFICATIONS:
 * Modified by Mangala Wallet for Kotlin Multiplatform compatibility.
 * ----------------------------------------------------------------
 */

package com.alphawallet.token.entity;

import java.util.Map;

/**
 * Created by JB on 23/05/2020.
 */
public abstract class EvaluateSelection
{
    private static final int STACK_CHECK = 10;

    public static boolean evaluate(TSFilterNode head, Map<String, TokenScriptResult.Attribute> attrs)
    {
        //evaluate from bottom up
        //evaluate each leaf logic
        //unevaluate all logic
        unevaluateAllNodes(head);
        evaluateLeafNodes(head, attrs);

        int stackCheck = STACK_CHECK; //prevent infinite loop in case of error

        while (stackCheck > 0 && head.logic == TSFilterNode.LogicState.NONE)
        {
            evaluateLogic(head);
            stackCheck--;
        }

        return (head.logic == TSFilterNode.LogicState.TRUE);
    }

    private static void unevaluateAllNodes(TSFilterNode node)
    {
        if (node.isNodeLogic() || node.isLeafLogic())
        {
            node.logic = TSFilterNode.LogicState.NONE;
        }

        if (node.first != null)
        {
            unevaluateAllNodes(node.first);
        }

        if (node.second != null)
        {
            unevaluateAllNodes(node.second);
        }
    }

    private static void evaluateLogic(TSFilterNode node)
    {
        //start evaluating logic nodes, start from the bottom
        if (node.isNodeLogic())
        {
            //check that children have been evaluated
            if (node.first.isEvaluated() && node.second.isEvaluated())
            {
                node.logic = node.evaluate();
            }
        }

        if (node.first != null)
        {
            evaluateLogic(node.first);
        }

        if (node.second != null)
        {
            evaluateLogic(node.second);
        }
    }

    private static void evaluateLeafNodes(TSFilterNode node, Map<String, TokenScriptResult.Attribute> attrs)
    {
        if (node.isLeafLogic())
        {
            //evaluate
            node.logic = node.evaluate(attrs);
        }

        if (node.first != null)
        {
            evaluateLeafNodes(node.first, attrs);
        }

        if (node.second != null)
        {
            evaluateLeafNodes(node.second, attrs);
        }
    }
}

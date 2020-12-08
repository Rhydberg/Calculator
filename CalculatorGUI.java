import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CalculatorGUI
{
    public static void main(String args[])
    {
        new FrontEnd();
        System.out.println("All is Fine");
    }
}

class FrontEnd extends Frame
{
    TextField display;
    TextField ans;
    Button[] buttons;
    FrontEnd()
    {
        initializeButtons();

        display =new TextField(37);
        ans=new TextField(10);
        ans.setEditable(false);

        Panel buttonPanel=new Panel(new GridLayout(4,4,2,2));

        for(int i=0;i<3;i++)
        {
            for(int j=1;j<=3;j++)
            {
                buttonPanel.add(buttons[i*3+j]);
            }
            buttonPanel.add(buttons[9+(i+1)]);
        }

        buttonPanel.add(buttons[14]);
        buttonPanel.add(buttons[0]);
        buttonPanel.add(buttons[13]);
        buttonPanel.add(buttons[15]);

        Panel textPanel=new Panel(new FlowLayout());
        textPanel.add(display);
        textPanel.add(new Label("    Ans:"));
        textPanel.add(ans);

        this.setLayout(new BorderLayout());
        this.add(textPanel, BorderLayout.NORTH);
        this.add(buttonPanel, BorderLayout.CENTER);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
        setSize(500,300);

        addListeners();
    }
    private void initializeButtons()
    {
        buttons=new Button[16];
        for(int i=0;i<10;i++)
        {
            buttons[i]=new Button(""+i);
        }
        buttons[10]=new Button("+");
        buttons[11]=new Button("-");
        buttons[12]=new Button("*");
        buttons[13]=new Button("/");
        buttons[14]=new Button(".");
        buttons[15]=new Button("=");
    }

    private void addListeners()
    {
        for(Button b:buttons)
        {
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    String lbl=e.getActionCommand();
                    toDisplay(lbl);
                }
            });
        }

        display.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char key=e.getKeyChar();
            }
        });

        display.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input=display.getText();
                System.out.println("Input: "+input);
                display.setText("");
                double result=Backend.compute(input);
                ans.setText(""+result);
            }
        });
    }
    private void toDisplay(String txt)
    {
        char c=txt.charAt(0);

        if(c!='=')
        {
            display.setText(display.getText() + txt);
        }
        else
        {
            String input=display.getText();
            display.setText("");
            System.out.println("Input: "+input);
            double result=Backend.compute(input);
            ans.setText(""+result);
        }
    }
}

class Backend
{
    static String[]list;
    static final String p="-+ /* ^";
    static final String op="-+/*^()";
    static Stack<String>stack;
    static ArrayList<String> postfix;
    static ArrayList<String> prefix;
    private static void parse(String input)
    {
        String temp="";
        for(char c:input.toCharArray())
        {
            if(op.indexOf(c)>=0)
            {
                if(temp.length()>0 && temp.charAt(temp.length()-1)!=' ')
                    temp+=" ";
                temp+=c+" ";
            }
            else
                temp+=c;
        }

        temp=temp.trim();
        System.out.println(temp);
        list=temp.trim().split(" ");
    }
    private static int compare(String op1, String op2)
    {
        if(p.indexOf(op1)>1+p.indexOf(op2))
            return 1;
        if(p.indexOf(op1)+1<p.indexOf(op2))
            return -1;
        if(Math.abs(p.indexOf(op1)-p.indexOf(op2))<=1)
            return 0;

        return Integer.MIN_VALUE;
    }
    private static void getPostfix()
    {
        postfix=new ArrayList<>();
        stack=new Stack<>();
        for(String x:list)
        {
            if(x.equals("("))
            {
                stack.push(x);
                continue;
            }
            if(x.equals(")"))
            {
                while(!stack.peek().equals("("))
                    postfix.add(stack.pop());
                stack.pop();
                continue;
            }

            if(!op.contains(x))
            {
                postfix.add(x);
                continue;
            }

            while(!stack.isEmpty() && (compare(x,stack.peek())<0 || (!x.equals("^") && compare(x,stack.peek())==0)))
            {
                postfix.add(stack.pop());
            }

            stack.push(x);
        }

        while(!stack.isEmpty())
            postfix.add(stack.pop());

        System.out.println("Postfix expression:\n"+postfix);
    }
    static double compute(String input)
    {
        parse(input);
        getPostfix();
        for(int i=0;i<postfix.size();i++)
        {
            String s=postfix.get(i);
            if(op.contains(s))
            {
                char o=postfix.get(i).charAt(0);
                double n1=Double.parseDouble(postfix.get(i-2));
                double n2=Double.parseDouble(postfix.get(i-1));
                postfix.remove(i-2);
                postfix.remove(i-2);
                double ans=0;
                switch(o)
                {
                    case '+': ans=(n1+n2);break;
                    case '-': ans=(n1-n2);break;
                    case '*': ans=(n1*n2);break;
                    case '/': ans=(1.0*n1/n2);break;
                    case '^': ans=(Math.pow(n1,n2));
                }
                i-=2;
                postfix.set(i,""+ans);
            }
        }

        return Double.parseDouble(postfix.get(0));
    }
}

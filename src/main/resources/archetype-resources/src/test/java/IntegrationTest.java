#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ${package};

import com.google.inject.AbstractModule;
import org.junit.Before;
import org.junit.Test;
import se.scalablesolutions.akka.config.ActiveObjectConfigurator;
import se.scalablesolutions.akka.config.JavaConfig.AllForOne;
import se.scalablesolutions.akka.config.JavaConfig.Component;
import se.scalablesolutions.akka.config.JavaConfig.LifeCycle;
import se.scalablesolutions.akka.config.JavaConfig.Permanent;
import se.scalablesolutions.akka.config.JavaConfig.RestartStrategy;
import se.scalablesolutions.akka.dispatch.Dispatchers;
import se.scalablesolutions.akka.dispatch.MessageDispatcher;
import static org.junit.Assert.*;

/**
 *
 * @author bobo
 */
public class IntegrationTest {

    ActiveObjectConfigurator conf = new ActiveObjectConfigurator();
//    Dispatcher d = new Dispatcher(false);

    @Before
    public void setup() {

        MessageDispatcher dispatcher = Dispatchers.newExecutorBasedEventDrivenDispatcher("test");
        conf.addExternalGuiceModule(new AbstractModule() {

            @Override
            protected void configure() {
                bind(ChatSession.class);
            }
        }).configure(new RestartStrategy(new AllForOne(), 3, 5000, new Class[]{Exception.class}),
                new Component[]{
                    new Component(ChatServer.class, new LifeCycle(new Permanent()), 10000, dispatcher)}).inject().supervise();
    }
    @Test
    public void integrationTest() throws InterruptedException {
        final ChatSession userOne = conf.getExternalDependency(ChatSession.class);
        userOne.setUserName("userOne");
        userOne.login();
        final ChatSession userTwo = conf.getExternalDependency(ChatSession.class);
        userTwo.setUserName("userTwo");
        userTwo.login();
        Thread.sleep(100); //to ensure login is done
        userOne.sendMessage("Hello");
        userTwo.sendMessage("Another Message 1");
        Thread.sleep(400); //wait for messages to finish
        assertEquals(2, userOne.getLog().size());
        assertEquals(2, userTwo.getLog().size());
        assertEquals(userTwo.getLog(), userOne.getLog());
        System.out.println("userOneLog: "+userOne.getLog());

    }
}

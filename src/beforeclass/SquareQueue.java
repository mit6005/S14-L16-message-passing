package beforeclass;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Squares integers.
 * Demonstrates using blocking queues for message passing between threads.
 */
class Squarer {
    
    private final BlockingQueue<Integer> in;
    private final BlockingQueue<SquareResult> out;
    // rep invariant:
    //   in, out != null
    
    /**
     * Make a squarer that will listen for requests and generate replies.
     */
    public Squarer(BlockingQueue<Integer> requests, BlockingQueue<SquareResult> replies) {
        this.in = requests;
        this.out = replies;
    }
    
    /**
     * Start handling squaring requests.
     */
    public void start() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        // block until a request arrives
                        int x = in.take();
                        
                        // compute the answer and send it back
                        int y = x*x;
                        out.put(new SquareResult(x, y));
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }).start();
    }
    
    // TODO: we will want a way to stop the thread
}

/** A squaring result message. */
class SquareResult {
    private final int input;
    private final int output;
    
    /** Make a new result message. */
    public SquareResult(int input, int output) {
        this.input = input;
        this.output = output;
    }
    
    // TODO: we will want more observers, but for now...
    
    @Override public String toString() {
        return input + "^2 = " + output;
    }
}

public class SquareQueue {
    
    /**
     * Create and use a squarer.
     */
    public static void main(String[] args) {
        
        BlockingQueue<Integer> requests = new LinkedBlockingQueue<Integer>();
        BlockingQueue<SquareResult> replies = new LinkedBlockingQueue<SquareResult>();
        
        Squarer squarer = new Squarer(requests, replies);
        squarer.start();
        
        try {
            // make a request
            requests.put(42);
            
            // maybe do something concurrently...
            
            // read the reply
            System.out.println(replies.take());
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}

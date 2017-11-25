package eisenwave.vv.ui.fmtvert;

public interface ProgressListener {
    
    /**
     * Observes a progress update.
     *
     * @param now the current progress (0 <= now <= max)
     * @param max the maximum progress
     * @param relative the calculated relative progress in range(0,1)
     */
    public void update(int now, int max, float relative);
    
}

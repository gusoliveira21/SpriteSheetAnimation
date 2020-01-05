//Boneco Andando

package com.example.adm.spritesheetanimation;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SpriteSheetAnimation extends Activity
{

    // Nosso objeto que irá manter a visão e a lógica de animação da folha de sprite
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Inicializa o gameView e configura-o como a view
        gameView = new GameView(this);
        setContentView(gameView);

    }

    // Aqui está a nossa implementação do GameView
    // É uma classe interna.
    // Observe como o a chave de abertura e fechamento está dentro de SpriteSheetAnimation

    // Observe que implementamos runnable, então nós temos
    // Uma thread e podemos sobrescrever o método de run.
    class GameView extends SurfaceView implements Runnable
    {

        // Este é o nossa thread
        Thread gameThread = null;

        // Isso é novo. Precisamos de um SurfaceHolder quando usamos Paint e Canvas em um tópico
        // Vamos ver isso em ação no método draw em breve.
        SurfaceHolder ourHolder;

        // Um booleano que vamos armar e desarmar quando o jogo está em execução ou não.
        volatile boolean playing;

        // Um Canvas e um objeto Paint
        Canvas canvas;
        Paint paint;

        // Esta variável rastreia a taxa de quadros do jogo
        long fps;

        // Isso é usado para ajudar a calcular o fps
        private long timeThisFrame;

        // Declara um objeto do tipo Bitmap
        Bitmap bitmapBob;

        // Bob começa sem se mover
        boolean isMoving = false;

        // Ele pode andar a 250 pixels por segundo
        float walkSpeedPerSecond = 250;

        // Ele começa 10 pixels da esquerda
        float bobXPosition = 10;

        // New para a animação da folha de sprite

        // Estes próximos dois valores podem ser como você gosta a medida que a relação não distorcça muito o sprite
        private int frameWidth = 400;
        private int frameHeight = 400;

        // Quantos quadros há na folha de sprite?
        private int frameCount = 5;

        // Start at the first frame - where else?
        private int currentFrame = 0;

        // Comece no primeiro quadro - onde mais?
        private long lastFrameChangeTime = 0;

        // Quanto tempo deve durar cada quadro
        private int frameLengthInMilliseconds = 100;

        // Um retângulo para definir uma área da folha de sprite que representa 1 quadro
        private Rect frameToDraw = new Rect(
                0,
                0,
                frameWidth,
                frameHeight);

        // Um rect que define uma área da tela na qual desenhar
        RectF whereToDraw = new RectF(
                bobXPosition, 0,
                bobXPosition + frameWidth,
                frameHeight);

        // Quando inicializamos (chamando new ()) no gameView
        // Este método construtor especial executa o método
        public GameView(Context context)
        {
            // A próxima linha de código pede a Classe SurfaceView para configurar o nosso objeto.
            super(context);

            // Inicializa ourHolder e objetos paint
            ourHolder = getHolder();
            paint = new Paint();

            // Carrega Bob do arquivo .png
            bitmapBob = BitmapFactory.decodeResource(this.getResources(), R.drawable.bob);

            // Escala o bitmap para o tamanho correto, precisamos fazer isso porque o Android automaticamente dimensiona bitmaps com base na densidade da tela
            bitmapBob = Bitmap.createScaledBitmap(bitmapBob,
                    frameWidth * frameCount,
                    frameHeight,
                    false);

            // Confirgura nosso booleano para true - game on!
             playing = true;

        }

        @Override
        public void run()
        {
            while (playing)
            {

                // Captura a hora atual em milissegundos no startFrameTime
                long startFrameTime = System.currentTimeMillis();

                // Atualiza o quadro
                update();

                // Desenha o quadro
                draw();

                // Calcule o fps neste quadro
                // Podemos então usar o resultado para tempo de animações e muito mais.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame >= 1)
                {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        // Tudo o que precisa ser atualizado está aqui
        // Em projetos posteriores, teremos dezenas (arrays) de objetos.
        // Também faremos outras coisas como a detecção de colisões.
        public void update()
        {

            // Se bob está em movimento (o jogador está tocando na tela)
            // então mova-o para a direita com base na velocidade do alvo e nos fps atuais.
            if (isMoving)
            {
                bobXPosition = bobXPosition + (walkSpeedPerSecond / fps);
            }

        }

        public void getCurrentFrame()
        {

            long time = System.currentTimeMillis();
            if (isMoving)
            {// Somente anima se Bob está se movendo
                if (time > lastFrameChangeTime + frameLengthInMilliseconds)
                {
                    lastFrameChangeTime = time;
                    currentFrame++;
                    if (currentFrame >= frameCount)
                    {

                        currentFrame = 0;
                    }
                }
            }
            // atualiza os valores esquerdo e direito da fonte do próximo quadro na folha de sprites
            frameToDraw.left = currentFrame * frameWidth;
            frameToDraw.right = frameToDraw.left + frameWidth;

        }

        // Desenhe a cena recém-atualizada
        public void draw()
        {

            // Certifica-se de que a nossa superfície de desenho é válida ou quebramos
            if (ourHolder.getSurface().isValid())
            {
                // Bloqueia o canvas para desenhar
                canvas = ourHolder.lockCanvas();

                // Desenha a cor de fundo
                canvas.drawColor(Color.RED);

                // Escolhe a cor da escova para desenhar
                paint.setColor(Color.argb(255, 249, 129, 0));

                // Faz o texto um pouco maior
                paint.setTextSize(45);

                // Exibe os fps atuais na tela
                canvas.drawText("FPS:" + fps, 20, 40, paint);

                // Desenha bob at bobXPosition, 200 pixels
                //canvas.drawBitmap(bitmapBob, bobXPosition, 200, paint);

                whereToDraw.set((int) bobXPosition,
                        0,
                        (int) bobXPosition + frameWidth,
                        frameHeight);

                getCurrentFrame();

                canvas.drawBitmap(bitmapBob,
                        frameToDraw,
                        whereToDraw, paint);

                // Desenha tudo para a tela
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        // Se a atividade SimpleGameEngine for pausada/parada encerra nossa thread.
        public void pause()
        {
            playing = false;
            try
            {
                gameThread.join();
            } catch (InterruptedException e)
            {
                Log.e("Error:", "joining thread");
            }

        }

        // Se a atividade SimpleGameEngine for iniciada, então, começa a thread
        public void resume()
        {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // A classe SurfaceView implementa onTouchListener, entao, podemos sobrescrever este método e detectar toques de tela.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent)
        {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK)
            {

                // O jogador tocou a tela
                case MotionEvent.ACTION_DOWN:

                    // Configura isMoving, para que Bob se mova
                    isMoving = true;

                    break;

                // O jogador removeu o dedo da tela
                case MotionEvent.ACTION_UP:

                    // Configura isMoving para que Bob não se mova
                    isMoving = false;

                    break;
            }
            return true;
        }

    }
    // Este é o fim da nossa classe interna GameView
    // Este método é executado quando o jogador começa o jogo
    @Override
    protected void onResume()
    {
        super.onResume();

        // Diz ao método retomar gameView para executar
        gameView.resume();
    }

    // Este método é executado quando o jogador encerra o jogo
    @Override
    protected void onPause()
    {
        super.onPause();

        // Indica para o método que pausa gameView executar
        gameView.pause();
    }

}
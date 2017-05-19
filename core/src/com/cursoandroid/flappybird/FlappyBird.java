package com.cursoandroid.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture[] passaros;
    private Texture fundo;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;
    private Random numeroRandomico;
    private BitmapFont fonte;
    private BitmapFont mensagem;
    private Circle passaroCirculo;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;
//    private ShapeRenderer shape;

    //Atributos de configuração
    private float variacao = 0;
    private float velocidadeQueda = 0;
    private float posicaoInicialVertical;
    private float posicaoMovimentoHorizontal;
    private float espacoEntreCano;
    private float deltaTime;
    private float alturaEntreCanosRondomicos;
    private boolean marcouPonto = false;

    //Camera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 768;
    private final float VIRTUAL_HEIGTH = 1024;

    //largura e altura da tela
    private float larguraDispositivo;
    private float alturaDispositivo;
    private int estadoJogo = 0;// aqui quando estado for 0 não inicia quando for 1 inicia
    private int pontuacao = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        numeroRandomico = new Random();
        passaroCirculo = new Circle();
//        retanguloCanoBaixo = new Rectangle();
//        retanguloCanoTopo = new Rectangle();
//        shape = new ShapeRenderer();

        fonte = new BitmapFont();
        fonte.setColor(Color.WHITE);
        fonte.getData().setScale(6);

        mensagem = new BitmapFont();
        mensagem.setColor(Color.WHITE);
        mensagem.getData().setScale(3);

        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        fundo = new Texture("fundo.png");
        canoBaixo = new Texture("cano_baixo.png");
        canoTopo = new Texture("cano_topo.png");
        gameOver = new Texture("game_over.png");

        //Configuração da Camera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH / 2, VIRTUAL_HEIGTH / 2,0);

        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGTH, camera);

        larguraDispositivo = VIRTUAL_WIDTH;
        alturaDispositivo = VIRTUAL_HEIGTH;

        posicaoInicialVertical = alturaDispositivo / 2;
        posicaoMovimentoHorizontal = larguraDispositivo;
        espacoEntreCano = 300;
    }

    @Override
    public void render() {

        deltaTime = Gdx.graphics.getDeltaTime();
        // Movimento do passaros
        variacao += deltaTime * 10;
        if (variacao > 2) variacao = 0; //aqui vai pegar somente os 3 passaros e ficar repetindo

        if (estadoJogo == 0) { //não iniciado
            if (Gdx.input.justTouched()) {
                estadoJogo = 1;
            }
        } else {

            velocidadeQueda++;
            if (posicaoInicialVertical > 0 || velocidadeQueda < 0)//aqui vai parar de cair quando chegar em 0
                posicaoInicialVertical = posicaoInicialVertical - velocidadeQueda;

            if (estadoJogo == 1){
                posicaoMovimentoHorizontal -= deltaTime * 200;
                if (Gdx.input.justTouched()) { //aqui vai pular
                    velocidadeQueda = -15;
                }
                //verificar se o cano saiu inteiramente da tela
                if (posicaoMovimentoHorizontal < -canoTopo.getWidth()) {
                    posicaoMovimentoHorizontal = larguraDispositivo;
                    alturaEntreCanosRondomicos = numeroRandomico.nextInt(400) - 200;
                    marcouPonto = false;
                }

                //Verificar a pontuação
                if (posicaoMovimentoHorizontal < 120) {
                    if (!marcouPonto) {
                        pontuacao++;
                        marcouPonto = true;
                    }
                }
            }else {//Tela de game over

                if (Gdx.input.justTouched()){
                    estadoJogo = 0;
                    pontuacao = 0;
                    velocidadeQueda = 0;
                    posicaoInicialVertical = alturaDispositivo / 2;
                    posicaoMovimentoHorizontal = larguraDispositivo;
                }
            }
        }

        // Configurar dados de projeto da camera
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(fundo, 0, 0, larguraDispositivo, alturaDispositivo);
        batch.draw(canoTopo, posicaoMovimentoHorizontal, alturaDispositivo / 2 + espacoEntreCano / 2 + alturaEntreCanosRondomicos);
        batch.draw(canoBaixo, posicaoMovimentoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCano / 2 + alturaEntreCanosRondomicos);
        batch.draw(passaros[(int) variacao], 120, posicaoInicialVertical);
        fonte.draw(batch, String.valueOf(pontuacao), larguraDispositivo / 2, alturaDispositivo - 50);

        if (estadoJogo == 2){
            batch.draw(gameOver,larguraDispositivo /2 - gameOver.getWidth() / 2, alturaDispositivo /2);
            mensagem.draw(batch,"Toque para Reiniciar!!",larguraDispositivo / 2 - 210, alturaDispositivo / 2 - gameOver.getHeight() / 2);
        }

        passaroCirculo.set(120 + passaros[0].getWidth() / 2 , posicaoInicialVertical + passaros[0].getHeight() / 2, passaros[0].getWidth() / 2);
        retanguloCanoBaixo = new Rectangle(
                posicaoMovimentoHorizontal, alturaDispositivo / 2 - canoBaixo.getHeight() - espacoEntreCano / 2 + alturaEntreCanosRondomicos,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );
        retanguloCanoTopo = new Rectangle(
                posicaoMovimentoHorizontal, alturaDispositivo / 2 + espacoEntreCano / 2 + alturaEntreCanosRondomicos,
                canoTopo.getWidth(), canoTopo.getHeight()
        );
        batch.end();

        //Desenhar formas
//        shape.begin(ShapeRenderer.ShapeType.Filled);
//        shape.circle(passaroCirculo.x, passaroCirculo.y, passaroCirculo.radius);
//        shape.rect(retanguloCanoBaixo.x, retanguloCanoBaixo.y, retanguloCanoBaixo.width, retanguloCanoBaixo.height);
//        shape.rect(retanguloCanoTopo.x, retanguloCanoTopo.y, retanguloCanoTopo.width, retanguloCanoTopo.height);
//        shape.setColor(Color.RED);
//        shape.end();

        //Teste de colisão
        if (Intersector.overlaps(passaroCirculo, retanguloCanoBaixo) || Intersector.overlaps(passaroCirculo, retanguloCanoTopo)
        || posicaoInicialVertical <= 0 || posicaoInicialVertical >= alturaDispositivo){
//            Gdx.app.log("Colisão","Houve colisão");
            estadoJogo = 2;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }
}

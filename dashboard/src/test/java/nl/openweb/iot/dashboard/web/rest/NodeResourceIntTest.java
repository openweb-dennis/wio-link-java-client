package nl.openweb.iot.dashboard.web.rest;

import javax.persistence.EntityManager;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import nl.openweb.iot.dashboard.DashboardApp;
import nl.openweb.iot.data.JpaNodeBean;
import nl.openweb.iot.data.JpaNodeRepository;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the NodeResource REST controller.
 *
 * @see NodeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DashboardApp.class)
public class NodeResourceIntTest {

    private static final String DEFAULT_NODE_SN = "AAAAAAAAAA";
    private static final String UPDATED_NODE_SN = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_NODE_KEY = "AAAAAAAAAA";
    private static final String UPDATED_NODE_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_DATA_X_SERVER = "AAAAAAAAAA";
    private static final String UPDATED_DATA_X_SERVER = "BBBBBBBBBB";

    private static final String DEFAULT_BOARD = "AAAAAAAAAA";
    private static final String UPDATED_BOARD = "BBBBBBBBBB";

    private static final Boolean DEFAULT_INITIALIZED = false;
    private static final Boolean UPDATED_INITIALIZED = true;

    @Autowired
    private JpaNodeRepository nodeRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private EntityManager em;

    private MockMvc restNodeMockMvc;

    private JpaNodeBean node;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NodeResource nodeResource = new NodeResource(nodeRepository);
        this.restNodeMockMvc = MockMvcBuilders.standaloneSetup(nodeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JpaNodeBean createEntity(EntityManager em) {
        JpaNodeBean node = new JpaNodeBean();
        node.setNodeSn(DEFAULT_NODE_SN);
        node.setName(DEFAULT_NAME);
        node.setNodeKey(DEFAULT_NODE_KEY);
        node.setDataXServer(DEFAULT_DATA_X_SERVER);
        node.setBoard(DEFAULT_BOARD);
        node.setInitialized(DEFAULT_INITIALIZED);
        return node;
    }

    @Before
    public void initTest() {
        node = createEntity(em);
    }

    @Test
    @Transactional
    public void createNode() throws Exception {
        int databaseSizeBeforeCreate = nodeRepository.findAll().size();

        // Create the JpaNodeBean

        restNodeMockMvc.perform(post("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(node)))
            .andExpect(status().isCreated());

        // Validate the JpaNodeBean in the database
        List<JpaNodeBean> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeCreate + 1);
        JpaNodeBean testNode = nodeList.get(nodeList.size() - 1);
        assertThat(testNode.getNodeSn()).isEqualTo(DEFAULT_NODE_SN);
        assertThat(testNode.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testNode.getNodeKey()).isEqualTo(DEFAULT_NODE_KEY);
        assertThat(testNode.getDataXServer()).isEqualTo(DEFAULT_DATA_X_SERVER);
        assertThat(testNode.getBoard()).isEqualTo(DEFAULT_BOARD);
        assertThat(testNode.getInitialized()).isEqualTo(DEFAULT_INITIALIZED);
    }

    @Test
    @Transactional
    public void createNodeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = nodeRepository.findAll().size();

        // Create the JpaNodeBean with an existing ID
        JpaNodeBean existingNode = new JpaNodeBean();
        existingNode.setNodeSn(DEFAULT_NODE_SN);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNodeMockMvc.perform(post("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(existingNode)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<JpaNodeBean> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = nodeRepository.findAll().size();
        // set the field null
        node.setName(null);

        // Create the JpaNodeBean, which fails.

        restNodeMockMvc.perform(post("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(node)))
            .andExpect(status().isBadRequest());

        List<JpaNodeBean> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNodeKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = nodeRepository.findAll().size();
        // set the field null
        node.setNodeKey(null);

        // Create the JpaNodeBean, which fails.

        restNodeMockMvc.perform(post("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(node)))
            .andExpect(status().isBadRequest());

        List<JpaNodeBean> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkInitializedIsRequired() throws Exception {
        int databaseSizeBeforeTest = nodeRepository.findAll().size();
        // set the field null
        node.setInitialized(null);

        // Create the JpaNodeBean, which fails.

        restNodeMockMvc.perform(post("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(node)))
            .andExpect(status().isBadRequest());

        List<JpaNodeBean> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllNodes() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get all the nodeList
        restNodeMockMvc.perform(get("/api/nodes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].nodeSn").value(hasItem(DEFAULT_NODE_SN.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].nodeKey").value(hasItem(DEFAULT_NODE_KEY.toString())))
            .andExpect(jsonPath("$.[*].dataXServer").value(hasItem(DEFAULT_DATA_X_SERVER.toString())))
            .andExpect(jsonPath("$.[*].board").value(hasItem(DEFAULT_BOARD.toString())))
            .andExpect(jsonPath("$.[*].initialized").value(hasItem(DEFAULT_INITIALIZED.booleanValue())));
    }

    @Test
    @Transactional
    public void getNode() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);

        // Get the node
        restNodeMockMvc.perform(get("/api/nodes/{id}", node.getNodeSn()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.nodeSn").value(DEFAULT_NODE_SN.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.nodeKey").value(DEFAULT_NODE_KEY.toString()))
            .andExpect(jsonPath("$.dataXServer").value(DEFAULT_DATA_X_SERVER.toString()))
            .andExpect(jsonPath("$.board").value(DEFAULT_BOARD.toString()))
            .andExpect(jsonPath("$.initialized").value(DEFAULT_INITIALIZED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingNode() throws Exception {
        // Get the node
        restNodeMockMvc.perform(get("/api/nodes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNode() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);
        int databaseSizeBeforeUpdate = nodeRepository.findAll().size();

        // Update the node
        JpaNodeBean updatedNode = nodeRepository.findOne(node.getNodeSn());
        updatedNode.setName(UPDATED_NAME);
        updatedNode.setNodeKey(UPDATED_NODE_KEY);
        updatedNode.setDataXServer(UPDATED_DATA_X_SERVER);
        updatedNode.setBoard(UPDATED_BOARD);
        updatedNode.setInitialized(UPDATED_INITIALIZED);

        restNodeMockMvc.perform(put("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedNode)))
            .andExpect(status().isOk());

        // Validate the JpaNodeBean in the database
        List<JpaNodeBean> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeUpdate);
        JpaNodeBean testNode = nodeList.get(nodeList.size() - 1);
        assertThat(testNode.getNodeSn()).isEqualTo(DEFAULT_NODE_SN);
        assertThat(testNode.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testNode.getNodeKey()).isEqualTo(UPDATED_NODE_KEY);
        assertThat(testNode.getDataXServer()).isEqualTo(UPDATED_DATA_X_SERVER);
        assertThat(testNode.getBoard()).isEqualTo(UPDATED_BOARD);
        assertThat(testNode.getInitialized()).isEqualTo(UPDATED_INITIALIZED);
    }

    @Test
    @Transactional
    public void updateNonExistingNode() throws Exception {
        int databaseSizeBeforeUpdate = nodeRepository.findAll().size();

        // Create the JpaNodeBean

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restNodeMockMvc.perform(put("/api/nodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(node)))
            .andExpect(status().isCreated());

        // Validate the JpaNodeBean in the database
        List<JpaNodeBean> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteNode() throws Exception {
        // Initialize the database
        nodeRepository.saveAndFlush(node);
        int databaseSizeBeforeDelete = nodeRepository.findAll().size();

        // Get the node
        restNodeMockMvc.perform(delete("/api/nodes/{id}", node.getNodeSn())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<JpaNodeBean> nodeList = nodeRepository.findAll();
        assertThat(nodeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}